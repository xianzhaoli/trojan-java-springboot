# trojan-java-springboot

基于 Java 实现的 Trojan 协议服务端，采用 Spring Boot 框架，适用于需要安全、高性能代理服务的场景。

## 项目简介

trojan-java-springboot 项目致力于用 Java/Spring Boot 实现 Trojan 协议的代理服务端。Trojan 协议以安全、高性能著称，广泛用于科学上网、隐私保护等领域。本项目提供 TLS 加密、流量统计、用户验证等功能，便于开发者与运维人员在 Java 生态下部署 Trojan 服务。

## 功能特性

- **TLS 加密**：全链路加密通信，保障数据安全。
- **流量统计**：支持实时统计各用户流量，为运营和管理提供依据。
- **用户验证**：多用户账号管理，支持密码验证，提升服务安全性。
- **高性能**：基于 Spring Boot，易于扩展和维护。
- **可配置**：灵活配置端口、用户、TLS 证书等参数。
- **日志管理**：详细日志输出，便于故障排查和运维。

## 安装与运行说明

### 依赖环境

- Java 17 或更高版本
- Maven 3.x
- Spring Boot 2.x
- 有效的 TLS 证书（可自签）

### 构建项目

```bash
git clone https://github.com/xianzhaoli/trojan-java-springboot.git
cd trojan-java-springboot
mvn clean package
```

### 配置参数

在 `src/main/resources/application.yml` 或 `application.properties` 文件中进行如下配置：

```yaml
trojan:
  port: 443
  tls:
    cert: /path/to/cert.pem
    key: /path/to/key.pem
logging:
  level:
    root: INFO
```

### 启动服务

```bash
java -jar target/trojan-java-springboot.jar
```

## 使用方法

### 服务端部署

1. 按上述方式配置并启动服务端。
2. 确保 443 端口（或自定义端口）已开放。

### 客户端连接配置示例

可使用兼容 Trojan 协议的客户端（如 Clash、Trojan-Qt5 等），示例配置：

```yaml
proxies:
  - name: "Trojan-Java-Server"
    type: trojan
    server: your.server.ip
    port: 443
    password: passwd1
    sni: your.domain.com
```

### 流量统计与日志

- 流量统计可通过后台管理页面或日志文件获取（如需开发可根据实际需求扩展）。
- 日志默认输出到控制台和 `logs/` 目录。

## 贡献指南

欢迎贡献代码、文档或反馈问题！

1. Fork 本仓库并创建分支。
2. 提交 PR 前请确保通过所有测试。
3. Issue 区欢迎提出 bug 报告或新功能建议。
4. 代码风格请遵循 Java 通用规范。

## License

本项目基于 MIT License 开源，详见 [LICENSE](./LICENSE)。

## 联系方式与相关链接

- 项目主页：[GitHub - trojan-java-springboot](https://github.com/xianzhaoli/trojan-java-springboot)
- 作者邮箱：xianzhaoli@gmail.com
- Trojan 协议官方文档：[https://trojan-gfw.github.io/trojan/](https://trojan-gfw.github.io/trojan/)

---
如有建议、需求或问题，欢迎通过 Issue 或邮件联系！
