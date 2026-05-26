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
mkdir -p /home/deploy/apps/robotics-lab-booking/{app,config,data,logs,backup}

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

说明：
- 如果服务器暂时不启用 Google OAuth，保持 `SPRING_PROFILES_ACTIVE=prod` 即可，不需要配置 `GOOGLE_CLIENT_ID` 和 `GOOGLE_CLIENT_SECRET`。
- 如果需要启用 Google OAuth，把 `SPRING_PROFILES_ACTIVE` 改成 `prod,oauth`，并额外加入：

```bash
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
GOOGLE_REDIRECT_URI=https://your-domain/login/oauth2/code/google
```

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
  -p 0.0.0.0:18080:80
```

查看 Pod：

```bash
podman pod ps -a
podman pod inspect robotics-lab-booking-pod
```

验收标准：
- `robotics-lab-booking-pod` 成功创建
- Pod 端口绑定为 `0.0.0.0:18080 -> 80`
- 第二项目 Pod 名与第一个项目完全不同

## 第 6 步：创建第二项目容器

目标：
- 在第二项目 Pod 中创建 Spring Boot 容器，并挂载独立数据、日志目录。

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

## 第 6.5 步：创建 Pod 内 Nginx 入口容器

目标：
- 在同一个 Pod 内增加 Nginx 入口，让外部访问 `18080` 先进入 Nginx，再转发到 Spring Boot 的 `8080`。

初始化命令：

```bash
mkdir -p /home/deploy/apps/robotics-lab-booking/nginx

cat > /home/deploy/apps/robotics-lab-booking/nginx/default.conf <<'EOF'
server {
    listen 80;
    server_name _;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;

        proxy_set_header Host $http_host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Host $http_host;

        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
EOF

podman run -d \
  --name robotics-lab-booking-nginx \
  --pod robotics-lab-booking-pod \
  -v /home/deploy/apps/robotics-lab-booking/nginx/default.conf:/etc/nginx/conf.d/default.conf:Z \
  docker.io/library/nginx:alpine
```

查看容器：

```bash
podman ps -a --format "table {{.Names}}\t{{.PodName}}\t{{.Status}}"
podman logs --tail 100 robotics-lab-booking-nginx
```

验收标准：
- `robotics-lab-booking-nginx` 成功创建并处于运行中
- `robotics-lab-booking-nginx` 与 `robotics-lab-booking-app` 位于同一个 Pod
- Nginx 日志中没有 `502 Bad Gateway`、`connect() failed` 等 upstream 错误

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

## 第 7.5 步：手动拉取新镜像并更新容器

目标：
- 当 GitHub Actions 已经发布新镜像到 GHCR 后，手动把服务器容器切到新版本。

推荐做法：
- 优先使用固定 tag，例如 `sha-提交短哈希`，避免 `latest` 指向变化带来回滚困难。
- 如果只是快速验证，也可以先用 `latest`。

先确认当前镜像：

```bash
podman ps --format "table {{.Names}}\t{{.Image}}\t{{.Status}}"
podman images | grep robotics-lab-booking-app
```

登录并拉取新镜像：

```bash
podman login ghcr.io
podman pull ghcr.io/lynn61liu/robotics-lab-booking-app:latest
```

如果你要拉固定版本，把上面的 `latest` 改成例如：

```bash
podman pull ghcr.io/lynn61liu/robotics-lab-booking-app:sha-<commit-sha>
```

停止并删除旧容器：

```bash
podman stop robotics-lab-booking-app
podman rm robotics-lab-booking-app
```

用新镜像重新创建容器：

```bash
podman run -d \
  --name robotics-lab-booking-app \
  --pod robotics-lab-booking-pod \
  --env-file /home/deploy/apps/robotics-lab-booking/config/.env \
  -v /home/deploy/apps/robotics-lab-booking/data:/app/data:Z \
  -v /home/deploy/apps/robotics-lab-booking/logs:/app/logs:Z \
  ghcr.io/lynn61liu/robotics-lab-booking-app:latest
```

如果要使用固定版本镜像，把最后一行改成例如：

```bash
ghcr.io/lynn61liu/robotics-lab-booking-app:sha-<commit-sha>
```

启动后检查：

```bash
podman ps -a
podman logs --tail 200 robotics-lab-booking-app
curl -I http://127.0.0.1:18080/
curl -s http://127.0.0.1:18080/getRole
```

如果新版本异常，回滚方法就是重新 `podman run` 回上一个可用的镜像 tag。

验收操作：

```bash
podman ps --format "table {{.Names}}\t{{.Image}}\t{{.Status}}"
podman logs --tail 200 robotics-lab-booking-app
curl -I http://127.0.0.1:18080/
curl -s http://127.0.0.1:18080/getRole
ls -lah /home/deploy/apps/robotics-lab-booking/data
```

验收标准：
- `robotics-lab-booking-app` 处于 `Up` 状态，没有反复退出
- 日志里能看到 Spring Boot 启动完成，没有 `Client id must not be empty`
- `curl -I http://127.0.0.1:18080/` 返回 `200`、`302` 或其他预期 HTTP 响应，而不是连接失败
- `curl -s http://127.0.0.1:18080/getRole` 能返回应用响应，例如 `ROLE_ADMIN`
- `/home/deploy/apps/robotics-lab-booking/data` 里的 H2 数据文件仍然存在，没有因为重建容器丢失

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
- 让第二个项目 Pod 和两个容器都可以通过 `systemd --user` 管理和自启动。

初始化命令：

```bash
mkdir -p /home/deploy/.config/systemd/user

podman generate systemd \
  --name robotics-lab-booking-pod \
  --files \
  --new

mv /home/deploy/pod-robotics-lab-booking-pod.service \
  /home/deploy/.config/systemd/user/

mv /home/deploy/container-robotics-lab-booking-app.service \
  /home/deploy/.config/systemd/user/

mv /home/deploy/container-robotics-lab-booking-nginx.service \
  /home/deploy/.config/systemd/user/

systemctl --user daemon-reload

systemctl --user enable pod-robotics-lab-booking-pod.service
systemctl --user enable container-robotics-lab-booking-app.service
systemctl --user enable container-robotics-lab-booking-nginx.service

systemctl --user start pod-robotics-lab-booking-pod.service
systemctl --user start container-robotics-lab-booking-app.service
systemctl --user start container-robotics-lab-booking-nginx.service
```

查看状态：

```bash
systemctl --user status pod-robotics-lab-booking-pod.service
systemctl --user status container-robotics-lab-booking-app.service
systemctl --user status container-robotics-lab-booking-nginx.service
```

验收标准：
- `pod-robotics-lab-booking-pod.service`、`container-robotics-lab-booking-app.service`、`container-robotics-lab-booking-nginx.service` 都已生成
- 三个 unit 均为 `enabled`
- `systemctl --user status ...` 显示服务正常运行
- unit 名称与第一个项目不冲突

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

## 第 12 步：验证 Pod 内 Nginx 转发入口

目标：
- 确认第二个项目已经通过 Pod 内 Nginx 暴露到 `IP:18080`，不需要修改宿主机 Nginx。

说明：
- 第一个项目使用了独立的 Nginx 容器，不适合直接复用。
- 第二个项目采用“同一个 Pod 内 Spring Boot + Nginx”的方式：
  - 宿主机 `18080`
  - -> Pod `80`
  - -> Nginx
  - -> Spring Boot `8080`
- `proxy_set_header Host $http_host` 用于保留原始 `IP:18080`，避免跳转丢端口。

初始化命令：

```bash
podman pod ps
podman ps -a --format "table {{.Names}}\t{{.PodName}}\t{{.Status}}\t{{.Ports}}"
podman logs --tail 100 robotics-lab-booking-app
podman logs --tail 100 robotics-lab-booking-nginx
curl -I http://127.0.0.1:18080/
curl -s http://127.0.0.1:18080/getRole
curl -I http://127.0.0.1:18080/equipmentList
```

验收标准：
- `robotics-lab-booking-pod` 为 `Running`
- `robotics-lab-booking-app` 和 `robotics-lab-booking-nginx` 都在同一个 Pod 内运行
- `curl -I http://127.0.0.1:18080/` 返回 `302`
- `curl -s http://127.0.0.1:18080/getRole` 返回 `ROLE_ADMIN`
- `curl -I http://127.0.0.1:18080/equipmentList` 返回 `200`
- Nginx 日志中看不到 `502 Bad Gateway`

## 第 13 步：外部访问验证

目标：
- 确认从浏览器访问第二个项目入口时，页面和接口都可用。

初始化命令：

```bash
curl -I http://<your-server-ip>:18080/
curl -I http://<your-server-ip>:18080/equipmentList
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
systemctl --user restart pod-robotics-lab-booking-pod.service
systemctl --user status pod-robotics-lab-booking-pod.service
systemctl --user status container-robotics-lab-booking-app.service
systemctl --user status container-robotics-lab-booking-nginx.service
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
- 第二项目拥有独立目录、独立 Pod、独立容器、独立 systemd unit、独立 `IP:18080` 入口
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
