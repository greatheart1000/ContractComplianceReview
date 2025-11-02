# 合同合规审查项目

### 根目录
```
complianceguardian/
├── compliance-guardian/          # 后端项目（Spring Boot）
├── compliance-guardian-ui/       # 管理端前端项目（Vue 3）
└── compliance-guardian-user/     # 用户端前端项目（Vue 3）
```

---

## 🎯 各子文件夹功能详解

### 1️⃣ **compliance-guardian/** - 后端服务

这是Spring Boot后端项目，提供所有业务逻辑和API接口。

#### 核心模块：

**`chat/`** - AI聊天合规模块
- `controller/` - 聊天接口控制器
  - `ChatComplianceController.java` - 聊天会话管理接口
  - `ModelFileController.java` - 文件上传接口
- `service/` - 业务逻辑层
  - `ChatComplianceService.java` - 聊天会话管理服务
  - `ContractCheckService.java` - 合同合规检查服务（核心业务）
  - `FileContentExtractService.java` - 文件内容提取服务
- `util/` - 工具类
  - `OpenAIChatUtil.java` - OpenAI对话工具
  - `PdfAliyunAnalysisUtil.java` - 阿里云PDF解析
  - `WordContentAnalysisUtil.java` - Word文档解析
  - `ImageAliyunAnalysisUtil.java` - 阿里云图片OCR
  - `ImageOpenAIAnalysisUtil.java` - OpenAI图片识别
  - `CacheSseUtil.java` - SSE连接缓存管理

**`contract/`** - 合同管理模块
- `controller/`
  - `ContractStandardController.java` - 标准合同规则管理
  - `ContractBusinessController.java` - 业务合同管理
- `service/` - 合同规则CRUD服务
- `model/` - 合同实体类

**`config/`** - 配置管理模块
- `controller/`
  - `ModelConfigController.java` - AI模型配置管理
  - `ModelUserManageController.java` - 模型用户管理
  - `ModelUserCenterController.java` - 用户中心接口
- `service/` - 配置管理服务（包含用户导入导出）

**`common/`** - 公共模块
- `annotation/` - 自定义注解（如`@ModelLoginVerify`）
- `constant/` - 常量定义
- `interceptor/` - 拦截器（登录验证等）
- `util/` - 公共工具类

**`param/`** - 参数工具模块
- `util/` - 其他AI工具（如豆包AI）

---

### 2️⃣ **compliance-guardian-ui/** - 管理端前端

这是完整的管理后台界面，包含所有管理功能。

#### 主要目录：

**`src/views/compliance/`** - 合规业务页面
- `config/` - 配置管理页面
  - `model/IndexList.vue` - 模型配置列表
  - `user/IndexList.vue` - 用户管理列表
- `contract/` - 合同管理
  - `standard/` - 标准规则管理页面
  - `business/` - 业务合同管理页面

**`src/views/yqvalley/`** - 系统管理页面
- `manage/` - 平台管理功能
  - `user/` - 用户管理
  - `role/` - 角色管理
  - `dept/` - 部门管理
  - `config/` - 系统配置
  - `log/` - 日志管理
  - `redis/` - 缓存管理
  - `file/` - 文件管理
- `develop/` - 开发管理功能
  - `menu/` - 菜单配置
  - `authority/` - 权限配置
  - `mailtemp/` - 邮件模板
- `user/` - 个人中心
  - `info/` - 个人信息
  - `password/` - 密码修改
  - `log/` - 操作记录

**`src/components/`** - 组件库
- `markdown/` - Markdown编辑器
- `tinymce/` - 富文本编辑器
- `yqvalley/` - 业务组件（上传、用户选择等）

**`src/router/`** - 路由配置

**`src/assets/`** - 静态资源
- `icons/` - SVG图标
- `img/` - 图片资源
- `less/` - 样式文件

**`public/tinymce/`** - TinyMCE富文本编辑器资源

---

### 3️⃣ **compliance-guardian-user/** - 用户端前端

这是面向普通用户的简化界面，主要用于合同合规检查和AI对话。

#### 主要目录：

**`src/components/chatgpt/`** - ChatGPT对话组件
- `window/chat/` - 聊天窗口组件
- `markdownview/` - Markdown视图组件

**`src/views/`** - 页面
- `Index.vue` - 主页（包含聊天功能）
- `Login.vue` - 登录页

**配置说明：**
- 端口：80（管理端是8443）
- 功能更精简，主要面向合同合规检查场景

---

## 🚀 如何使用项目

### 环境要求

1. **后端：**
   - JDK 21
   - Maven 3.6+
   - MySQL 5.7+
   - Redis

2. **前端：**
   - Node.js 14+
   - npm 或 yarn

3. **外部服务：**
   - OpenAI API Key（或阿里云OCR/PDF识别服务）
   - 阿里云OCR服务（可选）

---

### 步骤1：配置数据库和Redis

编辑 `compliance-guardian/src/main/resources/dev/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://你的数据库地址:3306/compliance?...
    username: 你的数据库用户名
    password: 你的数据库密码
  
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      password: 你的Redis密码
```

---

### 步骤2：配置AI服务

在 `application.yml` 中配置：

```yaml
spring:
  ai:
    openai:
      api-key: 你的OpenAI_API_KEY
      base-url: https://api.openai.com
```

---

### 步骤3：启动后端服务

```bash
# 进入后端目录
cd compliance-guardian

# 编译打包（使用dev环境）
mvn clean package -Pdev

# 或者直接在IDE中运行
# 运行 ComplianceApplication.java

# 后端服务将在 http://localhost:8080/compliance 启动
```

---

### 步骤4：启动管理端前端

```bash
# 进入管理端目录
cd compliance-guardian-ui

# 安装依赖
npm install

# 启动开发服务器
npm run serve

# 访问地址：http://localhost:8443
```

**开发环境配置：**
- 端口：8443
- 代理：自动代理到 `http://127.0.0.1:8080/compliance/`

---

### 步骤5：启动用户端前端（可选）

```bash
# 进入用户端目录
cd compliance-guardian-user

# 安装依赖
npm install

# 启动开发服务器
npm run serve

# 访问地址：http://localhost:80
```

---

## 📋 主要功能使用流程

### 1. 合同合规检查流程

1. **登录系统**
   - 访问管理端或用户端
   - 管理端：`http://localhost:8443`
   - 用户端：`http://localhost:80`

2. **配置标准规则**（管理端）
   - 进入"合同管理" → "标准规则"
   - 添加合规规则：规则名称、合同类型、检查内容、解决建议

3. **上传合同文件**
   - 进入"合同合规"或"AI聊天"功能
   - 上传PDF、Word或图片文件

4. **AI分析**
   - 系统自动提取文件内容
   - AI基于标准规则进行合规性分析
   - 实时返回分析结果和建议

5. **查看结果**
   - 查看合规检查报告
   - 查看AI对话记录
   - 下载分析结果

---

### 2. 模型配置管理

1. **配置AI模型**
   - 进入"配置管理" → "模型配置"
   - 添加或编辑AI模型信息（模型ID、API Key等）

2. **用户管理**
   - 进入"配置管理" → "用户管理"
   - 添加、导入或管理用户

---

## 🔧 构建生产版本

### 后端打包：

```bash
cd compliance-guardian

# 生产环境打包
mvn clean package -Ppro

# 生成jar文件：target/compliance-guardian-0.0.1-SNAPSHOT.jar
```

### 前端打包：

```bash
# 管理端
cd compliance-guardian-ui
npm run build
# 生成的文件在 dist/ 目录

# 用户端
cd compliance-guardian-user
npm run build
# 生成的文件在 dist/ 目录
```

---

## 🐳 Docker部署

后端已包含Dockerfile：

```bash
cd compliance-guardian
docker build -t compliance-guardian:latest .
docker run -p 8080:8080 compliance-guardian:latest
```

---

## 📝 注意事项

1. **文件上传目录**：开发环境配置为 `D:/`，生产环境需要修改
2. **数据库初始化**：需要先创建数据库并导入表结构
3. **Redis缓存**：确保Redis服务正常运行
4. **API Key安全**：生产环境不要将API Key提交到代码仓库
5. **端口冲突**：确保8080、8443、80端口未被占用
