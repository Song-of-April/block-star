# Gift for Mom 下载站

网页静态文件位于 `dist/`，其中：

- `index.html`：下载页面。
- `giftformom.apk`：Android Release 安装包。
- `game-preview.jpg`：游戏预览图。
- `favicon.svg`：网站图标。

## Cloudflare Pages 设置

连接 GitHub 仓库 `Song-of-April/block-star` 后使用以下配置：

- Production branch：`main`
- Framework preset：`None`
- Build command：留空
- Build output directory：`giftformom-site/dist`
- Root directory：留空

部署成功后，在 Pages 项目的 **Custom domains** 中添加：

```text
giftformom.aprilsong.xin
```

如果 `aprilsong.xin` 已经由同一个 Cloudflare 账号管理，Cloudflare 通常会自动创建需要的 DNS 记录。
