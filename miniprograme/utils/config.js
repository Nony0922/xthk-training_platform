/**
 * 后端 API 地址配置
 *
 * 【微信开发者工具模拟器】可用：
 *   http://localhost:8080
 *
 * 【手机真机预览/调试】必须用电脑局域网 IP，不能用 localhost，例如：
 *   http://192.168.1.100:8080
 *   （在 cmd 执行 ipconfig 查看 IPv4 地址）
 *
 * 使用前请确保：
 * 1. MySQL 已启动，并已执行 training.sql
 * 2. 后端已启动：cd demo && mvn spring-boot:run
 * 3. 开发者工具 → 详情 → 本地设置 → 勾选「不校验合法域名」
 */
const BASE_URL = 'http://localhost:8080'

module.exports = { BASE_URL }
