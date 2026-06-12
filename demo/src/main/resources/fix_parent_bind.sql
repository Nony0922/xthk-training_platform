-- 修复家长账号绑定（若 parent1 登录提示未绑定家长信息，执行本脚本）
USE training_platform;

-- 为 parent1 补建家长档案（若不存在）
INSERT INTO parent (user_id, name, phone, address)
SELECT u.id, u.name, u.phone, '成都市武侯区'
FROM sys_user u
WHERE u.username = 'parent1'
  AND NOT EXISTS (SELECT 1 FROM parent p WHERE p.user_id = u.id);

-- 修正 user_id 关联错误的家长记录
UPDATE parent p
JOIN sys_user u ON u.username = 'parent1'
SET p.user_id = u.id
WHERE p.phone = '13800000004' AND (p.user_id IS NULL OR p.user_id <> u.id);

-- 修正学生与家长的关联
UPDATE student s
JOIN parent p ON p.phone = '13800000004'
SET s.parent_id = p.id
WHERE s.name IN ('王小明', '李小红');
