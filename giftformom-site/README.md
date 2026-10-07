# Gift for Mom 下载站

网页静态文件位于 `dist/`，其中：

- `index.html`：下载页面。
- `giftformom.apk`：Android Release 安装包。
- `game-preview.jpg`：游戏预览图。
- `favicon.svg`：网站图标。

## 当前 Cloudflare Workers 设置

连接 GitHub 仓库 `Song-of-April/block-star` 后使用以下配置：

- Production branch：`main`
- Worker 名称：`gift`
- Root directory：`/`
- Build command：留空
- Deploy command：`npx wrangler deploy`

仓库根目录的 `wrangler.jsonc` 已指定 `giftformom-site/dist` 为静态资源目录。部署成功后，在 Worker 的 **Domains & Routes** 中添加：

```text
giftformom.aprilsong.xin
```

如果 `aprilsong.xin` 已经由同一个 Cloudflare 账号管理，Cloudflare 通常会自动创建需要的 DNS 记录。
