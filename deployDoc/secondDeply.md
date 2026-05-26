# Robotics Lab Equipment Booking and Tracking System 部署步骤

适用场景：
- 共享 Linux 主机
- `deploy` 用户
- `rootless Podman`
- `systemd --user`
- 宿主机反向代理
- 第二个项目目标为单容器 + 本地 H2 文件库 + 持久化挂载

约定命名：
- 项目名：`robotics-lab-booking`
- Pod 名：`robotics-lab-booking-pod`
- 容器名：`robotics-lab-booking-app`
- systemd 服务名：`robotics-lab-booking.service`
- 部署根目录：`/home/deploy/apps/robotics-lab-booking`
- 应用目录：`/home/deploy/apps/robotics-lab-booking/app`
- 配置目录：`/home/deploy/apps/robotics-lab-booking/config`
- 数据目录：`/home/deploy/apps/robotics-lab-booking/data`
- 日志目录：`/home/deploy/apps/robotics-lab-booking/logs`
- 备份目录：`/home/deploy/apps/robotics-lab-booking/backup`
- 宿主机监听端口示例：`18080`
- 容器内应用端口：`8080`

注意事项：
- 不要复用第一个项目的 Pod、容器名、systemd service、反向代理规则、数据目录。
- 本文只针对第二个项目新增资源，不修改第一个项目已有资源。

## 第 1 步：确认当前环境

目标：
- 确认当前用户、Podman、systemd user、反向代理环境可用。

初始化命令：

```bash
whoami
id
which podman
podman --version
systemctl --user status
loginctl show-user "$(whoami)"
```

验收标准：
- 当前用户为 `deploy`
- `podman --version` 能正常输出版本
- `systemctl --user status` 能正常执行
- 用户会话处于可管理状态，没有明显权限报错

## 第 2 步：创建第二项目目录

目标：
- 为第二个项目建立独立部署目录、配置目录、数据目录、日志目录、备份目录。

初始化命令：

```bash
mkdir -p /home/deploy/apps/robotics-lab-booking/{app,config,data,logs,backup}
find /home/deploy/apps/robotics-lab-booking -maxdepth 2 -type d | sort
```

验收标准：
- 能看到以下目录都已创建：
  - `/home/deploy/apps/robotics-lab-booking/app`
  - `/home/deploy/apps/robotics-lab-booking/config`
  - `/home/deploy/apps/robotics-lab-booking/data`
  - `/home/deploy/apps/robotics-lab-booking/logs`
  - `/home/deploy/apps/robotics-lab-booking/backup`

## 第 3 步：确认第二项目镜像信息

目标：
- 确认第二个项目的镜像名和镜像 tag，不与第一个项目冲突。

初始化命令：

```bash
echo "IMAGE=ghcr.io/lynn61liu/robotics-lab-booking-app:latest"
```

如果服务器可以访问镜像仓库，也可检查：

```bash
podman login ghcr.io
podman pull ghcr.io/lynn61liu/robotics-lab-booking-app:latest
```

验收标准：
- 已确定第二项目独立镜像名
- 镜像名不与第一个项目镜像重复
- 如已执行 `pull`，镜像可被 Podman 正常拉取

## 第 4 步：准备环境变量文件

目标：
- 为第二项目准备独立配置，不复用第一个项目 env。

初始化命令：

```bash
cat > /home/deploy/apps/robotics-lab-booking/config/.env <<'EOF'
APP_NAME=robotics-lab-booking
APP_PORT=8080
HOST_PORT=18080
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:h2:file:/app/data/robotics-lab-booking
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=
JAVA_OPTS=-Xms256m -Xmx512m
EOF

cat /home/deploy/apps/robotics-lab-booking/config/.env
```

验收标准：
- `/home/deploy/apps/robotics-lab-booking/config/.env` 已创建
- 其中包含独立的 `HOST_PORT`
- 其中包含 H2 文件库路径，且路径指向第二项目自己的数据目录

## 第 5 步：准备 Podman Pod

目标：
- 创建第二项目自己的 Pod，不放进第一个项目的 Pod。

初始化命令：

先检查是否已存在同名 Pod：

```bash
podman pod ps -a
```

创建 Pod：

```bash
podman pod create \
  --name robotics-lab-booking-pod \
  -p 127.0.0.1:18080:8080
```

查看 Pod：

```bash
podman pod ps -a
podman pod inspect robotics-lab-booking-pod
```

验收标准：
- `robotics-lab-booking-pod` 成功创建
- Pod 端口绑定为 `127.0.0.1:18080 -> 8080`
- 第二项目 Pod 名与第一个项目完全不同

## 第 6 步：创建第二项目容器

目标：
- 在第二项目 Pod 中创建 Spring Boot 单容器，并挂载独立数据、日志目录。

初始化命令：

```bash
podman run -d \
  --name robotics-lab-booking-app \
  --pod robotics-lab-booking-pod \
  --env-file /home/deploy/apps/robotics-lab-booking/config/.env \
  -v /home/deploy/apps/robotics-lab-booking/data:/app/data:Z \
  -v /home/deploy/apps/robotics-lab-booking/logs:/app/logs:Z \
  ghcr.io/lynn61liu/robotics-lab-booking-app:latest
```

查看容器：

```bash
podman ps -a
podman inspect robotics-lab-booking-app
```

验收标准：
- `robotics-lab-booking-app` 成功创建并处于运行中
- 容器位于 `robotics-lab-booking-pod` 中
- `/app/data` 和 `/app/logs` 已挂载到第二项目独立目录

## 第 7 步：检查应用启动日志

目标：
- 确认第二个项目后端服务已经正常启动。

初始化命令：

```bash
podman logs --tail 200 robotics-lab-booking-app
```

持续查看：

```bash
podman logs -f robotics-lab-booking-app
```

验收标准：
- 日志里能看到 Spring Boot 正常启动
- 没有端口占用错误
- 没有数据库初始化致命错误
- 没有容器反复退出/重启现象

## 第 8 步：检查 H2 数据文件是否生成

目标：
- 确认本地 H2 文件库已真正创建到持久化目录。

初始化命令：

```bash
ls -lah /home/deploy/apps/robotics-lab-booking/data
```

验收标准：
- 能看到类似以下文件：
  - `robotics-lab-booking.mv.db`
  - `robotics-lab-booking.trace.db`（如有）
  - 或其他 H2 相关锁文件
- 数据文件位于第二项目自己的 `data` 目录，而不是第一个项目目录

## 第 9 步：本机访问验证

目标：
- 先从服务器本机验证第二个项目服务可访问。

初始化命令：

```bash
curl -I http://127.0.0.1:18080/
curl -s http://127.0.0.1:18080/getRole
```

验收标准：
- 根路径返回 `200` 或 `302`
- 核心接口有正常响应
- 第二项目在本机已可访问

## 第 10 步：生成 systemd user service

目标：
- 让第二个项目可以通过 `systemd --user` 管理和自启动。

初始化命令：

```bash
mkdir -p /home/deploy/.config/systemd/user
podman generate systemd \
  --name robotics-lab-booking-pod \
  --files \
  --new
mv pod-robotics-lab-booking-pod.service /home/deploy/.config/systemd/user/robotics-lab-booking.service
systemctl --user daemon-reload
systemctl --user enable robotics-lab-booking.service
systemctl --user start robotics-lab-booking.service
```

查看状态：

```bash
systemctl --user status robotics-lab-booking.service
```

验收标准：
- `robotics-lab-booking.service` 已生成
- `systemctl --user status robotics-lab-booking.service` 显示服务正常运行
- 服务名与第一个项目 service 不冲突

## 第 11 步：配置开机自启会话保活

目标：
- 确保 `deploy` 用户退出后，user service 仍可继续运行。

初始化命令：

```bash
loginctl enable-linger deploy
loginctl show-user deploy | grep Linger
```

验收标准：
- `Linger=yes`
- `deploy` 用户退出登录后，`systemd --user` 服务仍可保活

## 第 12 步：新增宿主机反向代理规则

目标：
- 为第二个项目新增一条独立入口，不影响第一个项目。

做法示例：
- 独立子域名：`robotics-lab-booking.example.com`
- 或独立路径：`/robotics-lab-booking`

初始化命令：

这一步要在宿主机反向代理配置里新增一条规则，具体命令取决于你们实际使用的是 Nginx、Caddy 还是 Traefik。

如果是 Nginx，至少要新增一条把请求转发到：

```text
http://127.0.0.1:18080
```

变更后检查配置：

```bash
sudo nginx -t
sudo systemctl reload nginx
```

验收标准：
- 第二个项目有独立域名或独立路径
- 反向代理目标指向 `127.0.0.1:18080`
- 第一个项目原有反向代理规则未被覆盖
- `nginx -t` 通过

## 第 13 步：外部访问验证

目标：
- 确认从浏览器访问第二个项目入口时，页面和接口都可用。

初始化命令：

```bash
curl -I http://<your-demo-domain-or-path>
```

手工验证：
- 浏览器打开第二个项目入口
- 查看首页是否正常加载
- 查看关键页面是否能打开
- 查看主要接口是否能返回数据

验收标准：
- 浏览器可正常打开第二个项目
- 页面资源加载正常
- 核心接口无 500 报错
- 第一个项目访问不受影响

## 第 14 步：重启验证

目标：
- 验证第二个项目在重启后仍能恢复，且 H2 数据不丢。

初始化命令：

```bash
systemctl --user restart robotics-lab-booking.service
systemctl --user status robotics-lab-booking.service
ls -lah /home/deploy/apps/robotics-lab-booking/data
curl -I http://127.0.0.1:18080/
```

验收标准：
- service 重启成功
- H2 数据文件仍存在
- 应用可再次访问
- 数据没有因为重启丢失

## 第 15 步：备份当前数据目录

目标：
- 为该环境保留最小回滚能力。

初始化命令：

```bash
tar -czf /home/deploy/apps/robotics-lab-booking/backup/robotics-lab-booking-$(date +%F-%H%M%S).tar.gz \
  /home/deploy/apps/robotics-lab-booking/data

ls -lah /home/deploy/apps/robotics-lab-booking/backup
```

验收标准：
- `backup` 目录下生成新的备份压缩包
- 备份文件名带时间戳

## 最终验收清单

最终需要全部满足：
- 第二项目拥有独立目录、独立 Pod、独立容器、独立 service、独立反向代理入口
- 第二项目可通过浏览器正常访问
- 第二项目 H2 数据文件落在自己的持久化目录中
- 第二项目重启后数据仍在
- 第一个项目访问不受任何影响

## 禁止事项

以下操作不要做：
- 不要把第二项目容器加进第一个项目现有 Pod
- 不要复用第一个项目的数据目录
- 不要复用第一个项目的容器名、Pod 名、service 名
- 不要直接改第一个项目的反向代理目标
- 不要让第二项目直接覆盖第一个项目的端口绑定
