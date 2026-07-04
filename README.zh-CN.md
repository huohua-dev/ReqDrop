<div align="center">

# ReqDrop

**为 Burp Suite 打造的基于规则的请求拦截扩展。**

按你定义的 host / path 规则静默丢弃 Proxy 请求——在遥测、统计打点和吵闹的第三方调用离开你的机器之前就干掉它们。

[![Release](https://github.com/huohua-dev/ReqDrop/actions/workflows/release.yml/badge.svg)](https://github.com/huohua-dev/ReqDrop/actions/workflows/release.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
![Java 17+](https://img.shields.io/badge/Java-17%2B-orange.svg)
![Burp Montoya API](https://img.shields.io/badge/Burp-Montoya%20API-e8703a.svg)

[English](README.md) · **简体中文**

</div>

---

## 功能简介

ReqDrop 会在 Burp Suite 中添加一个 **ReqDrop** 标签页。每一个经过 **Proxy** 的请求都会与你的规则列表逐条比对；一旦命中某条已启用的规则，该请求就会被丢弃——Burp 不会再转发它，同时你能在丢弃日志里看到这条记录。

它是清理 Proxy 历史噪声最快的方式：更新检测、崩溃上报、广告与统计端点，或者测试期间任何你不希望目标去访问的 host，都能被拦下。

## 特性

- **支持 host 和/或 path 匹配** —— 可只针对 host、只针对 path，或两者同时匹配。
- **两种匹配模式** —— **通配符**（`*`、`?`）用于快速编写，或完整的 **正则** 用于精确控制。
- **总开关** —— 一键开启/关闭全部拦截，无需删除任何规则。
- **单条规则启用/禁用** —— 直接在表格里通过复选框切换某条规则。
- **实时丢弃日志** —— 保留最近 2000 条被丢弃的请求，显示时间、方法、host、URL 以及命中的规则。
- **命中计数** —— 一眼看清每条规则触发了多少次。
- **持久化** —— 规则与设置会随 Burp 项目保存，重新加载时自动恢复。
- **失败即放行** —— 若规则内部出错，请求会被正常放行，绝不会被静默吞掉。
- **轻量** —— 不打包任何依赖的瘦 jar，Burp API 在运行时由 Burp 提供。

## 安装

1. 从 [最新 release](https://github.com/huohua-dev/ReqDrop/releases/latest) 下载 `reqdrop-<version>.jar`。
2. 在 Burp Suite 中进入 **Extensions → Installed → Add**。
3. 将 **Extension type** 设为 **Java**，选择该 jar，然后点击 **Next**。
4. 主工具栏中会出现 **ReqDrop** 标签页。

> 需要支持 Montoya API 的 Burp Suite 版本（Burp 2023.1 及以后）。

## 使用

1. 打开 **ReqDrop** 标签页，确认 **Enable ReqDrop** 处于勾选状态。
2. 点击 **Add** 新建规则：
   - **Host pattern** —— 例如 `*.google-analytics.com`（host 匹配不区分大小写）。
   - **Path pattern** —— 例如 `/telemetry/*`。任一字段留空表示匹配任意值。
   - **Match mode** —— **Wildcard** 或 **Regex**。
   - **Comment** —— 可选备注，帮你记住这条规则的用途。
3. 像平常一样让目标流量经过 Burp Proxy。命中的请求会被丢弃，并出现在规则表格下方的 **Dropped requests** 日志中。

**规则示例**

| Host pattern | Path pattern | 模式 | 拦截内容 |
|---|---|---|---|
| `*.google-analytics.com` | | Wildcard | 所有 Google Analytics 流量 |
| `sentry.io` | `/api/*/store/` | Wildcard | Sentry 崩溃上报接口 |
| | `\.(png\|jpg\|gif\|woff2?)$` | Regex | 静态图片/字体请求 |

规则自上而下依次匹配，第一条命中的规则生效。

## 从源码构建

构建需要 **JDK 21**（扩展本身编译为 Java 17 字节码）。

```bash
git clone https://github.com/huohua-dev/ReqDrop.git
cd ReqDrop
./gradlew build
```

构建产物位于 `build/libs/reqdrop.jar`。

## 发布

推送 `v*` 格式的 tag 会触发 [release 工作流](.github/workflows/release.yml)，它会在 JDK 21 上构建并测试项目，然后将 `reqdrop-<tag>.jar` 作为 GitHub release 资产发布。

```bash
git tag v0.1.0
git push origin v0.1.0
```

## 许可证

基于 [MIT License](LICENSE) 发布。
