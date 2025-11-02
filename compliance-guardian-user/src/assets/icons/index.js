//获取当前目录所有为.svg的文件
const req = require.context("@/assets/icons/svg", false, /\.svg$/);
// 解析获取的svg文件名称并返回
const requireAll = (requireContext) =>
  requireContext.keys().map(requireContext);
requireAll(req);