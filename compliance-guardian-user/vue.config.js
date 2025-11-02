const { defineConfig } = require("@vue/cli-service");
const path = require("path");
const CompressionPlugin = require("compression-webpack-plugin");
module.exports = defineConfig({
  transpileDependencies: [
    /* string or regex */
  ],
  publicPath: "./",

  // 是否在保存的时候使⽤ `eslint-loader` 进⾏检查。
  pages: {
    index: {
      // ⼊⼝⽂件
      entry: "src/main.ts" /*这个是根⼊⼝⽂件*/,
      // 模板⽂件
      template: "public/index.html", // 输出⽂件
      filename: "index.html", // 页⾯title
      title: "企业合规",
    }, // 简写格式
    // 模板⽂件默认是 `public/subpage.html`
    // 如果不存在，就是 `public/index.html`.
    // 输出⽂件默认是 `subpage.html`.
    subpage: "src/main.ts" /*注意这个是*/,
  },

  // 有效的值：`ture` | `false` | `"error"`
  // 当设置为 `"error"` 时，检查出的错误会触发编译失败。
  // 使⽤带有浏览器内编译器的完整构建版本
  //lintOnSave: true,
  lintOnSave: process.env.NODE_ENV !== "production",

  // 是否为⽣产环境构建⽣成 source map【生产打包改为false】
  productionSourceMap: true,

  // 查阅 https://github.com/vuejs/vue-docs-zh-cn/blob/master/vue-cli/webpack.md
  chainWebpack: (config) => {
    config.resolve.alias
      .set('@', path.resolve(__dirname, "./src"));
    //设置 svg-sprite-loader
    config.module
      .rule('svg')
      .exclude.add(path.resolve(__dirname,'./src/assets/icons'))
      .end()
    config.module
      .rule('icons')
      .test(/\.svg$/)
      .include.add(path.resolve(__dirname,'./src/assets/icons'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({
        symbolId: 'icon-[name]'
      })
      .end()
  },

  configureWebpack: (config) => {
    const prodPlugins = [
      new CompressionPlugin({
        filename: '[path][base].gz', // 压缩后的文件名(保持原文件名，后缀加上.gz)
        algorithm: 'gzip', // 开启 Gzip 压缩
        test: new RegExp('\\.(' + ['js', 'css'].join('|') + ')$'),
        threshold: 10240,// 对超过10k的数据压缩
        minRatio: 0.8, // 只有压缩率小于这个值的资源才会被处理
      }),
    ];
    //测试环境插件
    const devPlugins = [];
    if (process.env.NODE_ENV === 'production') {// 为生产环境修改配置...
      config.mode = 'production';
      config["performance"] = {//打包文件大小配置
        "maxEntrypointSize": 10000000,
        "maxAssetSize": 30000000
      }
      config.plugins = [...config.plugins, ...prodPlugins]
    } else {
      // 为生产环境修改配置...
      config.mode = "development";
      config.plugins = [...config.plugins, ...devPlugins]
    }
  },
  pluginOptions: {
    "style-resources-loader": {
      preProcessor: "less",
      patterns: [
        //存放less变量文件的路径
        path.resolve(__dirname, "./src/assets/less/main.less"),
      ],
    },
  },
  //CSS相关选项
  css: {
    loaderOptions: {
      less: {
        lessOptions: {
          modifyVars: {
            "primary-color": "#ec6800",
          },
          javascriptEnabled: true,
        },
      },
    },
  },
  pwa: {},
  // 配置webpack-dev-server⾏为。
  devServer: {
    open: process.platform === "win33",
    host: "localhost",
    port: 80,
    https: false,
    compress: false,
    proxy: {
      "/compliance/": {
        target: "http://127.0.0.1:8080/",
        changeOrigin: true, //允许跨域
        secure: false,
        ws: true,
        pathRewrite: {
          "^/": "/",
        },
      },
    }
  },
});
