<div align="center">

# ReqDrop

**Rule-based request blocking for Burp Suite.**

Silently drop Proxy requests that match host/path patterns you define — kill telemetry, analytics beacons, and noisy third-party calls before they ever leave your machine.

[![Release](https://github.com/huohua-dev/ReqDrop/actions/workflows/release.yml/badge.svg)](https://github.com/huohua-dev/ReqDrop/actions/workflows/release.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
![Java 17+](https://img.shields.io/badge/Java-17%2B-orange.svg)
![Burp Montoya API](https://img.shields.io/badge/Burp-Montoya%20API-e8703a.svg)

**English** · [简体中文](README.zh-CN.md)

</div>

---

## What it does

ReqDrop adds a **ReqDrop** tab to Burp Suite. Every request that passes through the **Proxy** is checked against your rule list; if it matches an enabled rule, the request is dropped — Burp never forwards it, and you see it recorded in the drop log.

It's the fastest way to stop the noise that clutters your Proxy history: update pings, crash reporters, ad and analytics endpoints, or any host you simply don't want your target talking to during a test.

## Features

- **Host and/or path matching** — write a rule against the host, the path, or both.
- **Two match modes** — **Wildcard** (`*`, `?`) for quick rules, or full **Regex** for precise control.
- **Master switch** — toggle all blocking on/off without deleting a single rule.
- **Per-rule enable/disable** — flip individual rules from the table with a checkbox.
- **Live drop log** — the last 2000 dropped requests, showing time, method, host, URL, and which rule caught them.
- **Hit counters** — see how many times each rule has fired, at a glance.
- **Persistent** — rules and settings are saved with your Burp project and restored on reload.
- **Fails open** — if a rule errors internally, the request is passed through, never silently lost.
- **Lightweight** — a thin jar with no bundled dependencies; the Burp API is provided at runtime.

## Install

1. Download `reqdrop-<version>.jar` from the [latest release](https://github.com/huohua-dev/ReqDrop/releases/latest).
2. In Burp Suite, go to **Extensions → Installed → Add**.
3. Set **Extension type** to **Java**, select the jar, and click **Next**.
4. The **ReqDrop** tab appears in the main toolbar.

> Requires a Burp Suite version that supports the Montoya API (Burp 2023.1 and later).

## Usage

1. Open the **ReqDrop** tab and make sure **Enable ReqDrop** is checked.
2. Click **Add** to create a rule:
   - **Host pattern** — e.g. `*.google-analytics.com` (host matching is case-insensitive).
   - **Path pattern** — e.g. `/telemetry/*`. Leave either field blank to match any value.
   - **Match mode** — **Wildcard** or **Regex**.
   - **Comment** — an optional note to remind you what the rule is for.
3. Route your target's traffic through Burp Proxy as usual. Matching requests are dropped and appear in the **Dropped requests** log below the rule table.

**Example rules**

| Host pattern | Path pattern | Mode | Blocks |
|---|---|---|---|
| `*.google-analytics.com` | | Wildcard | All Google Analytics traffic |
| `sentry.io` | `/api/*/store/` | Wildcard | Sentry crash-report ingestion |
| | `\.(png\|jpg\|gif\|woff2?)$` | Regex | Static image/font requests |

Rules are evaluated top to bottom; the first match wins.

## Build from source

Requires **JDK 21** to build (the extension itself targets Java 17 bytecode).

```bash
git clone https://github.com/huohua-dev/ReqDrop.git
cd ReqDrop
./gradlew build
```

The extension jar is written to `build/libs/reqdrop.jar`.

## Releases

Pushing a `v*` tag triggers the [release workflow](.github/workflows/release.yml), which builds and tests the project on JDK 21 and publishes `reqdrop-<tag>.jar` as a GitHub release asset.

```bash
git tag v0.1.0
git push origin v0.1.0
```

## License

Released under the [MIT License](LICENSE).
