//自定义图标
const req = require.context('@/assets/icons/svg', false, /\.svg$/)
const requireAll = requireContext => requireContext.keys()
const re = /\.\/(.*)\.svg/
const svgIcons = requireAll(req).map(i => {
  return i.match(re)[1]
})
//FontAwesome solid
const fasIcons=[
  "fas fa-magnifying-glass", 
  "fas fa-arrows-rotate",
  "fas fa-save",
  "fas fa-file-invoice",
  "fas fa-folder-plus",
  "fas fa-sitemap",
  "fas fa-outdent",
  "fas fa-indent",
  "fas fa-home",
  "fas fa-angle-down",
  "fas fa-address-card",
  "fas fa-key",
  "fas fa-arrows-alt",
  "fas fa-user",
  "fas fa-user-plus",
  "fas fa-users",
  "fas fa-user-tie",
  "fas fa-refresh",
  "fas fa-search",
  "fas fa-reply",
  "fas fa-plus",
  "fas fa-download",
  "fas fa-angle-double-down",
  "fas fa-angle-double-up",
  "fas fa-eye",
  "fas fa-eye-slash",
  "fas fa-bars",
  "fas fa-barcode",
  "fas fa-info-circle",
  "fas fa-plus-circle",
  "fas fa-trash-alt",
  "fas fa-pencil-square",
  "fas fa-file-excel",
  "fas fa-angle-right",
  "fas fa-flask",
  "fas fa-paper-plane",
  "fas fa-arrow-circle-down",
  "fas fa-align-left",
  "fas fa-edit",
  "fas fa-cog",
  "fas fa-cogs",
  "fas fa-ban",
  "fas fa-undo",
  "fas fa-upload",
  "fas fa-retweet",
  "fas fa-exclamation-circle",
  "fas fa-shield",
  "fas fa-tasks",
  "fas fa-laptop-code",
  "fas fa-globe",
  "fas fa-list-alt",
  "fas fa-boxes",
  "fas fa-dolly-flatbed",
  "fas fa-cart-plus",
  "fas fa-bar-chart",
  "fas fa-binoculars",
  "fas fa-sack-dollar",
  "fas fa-user-shield",
  "fas fa-wrench",
  "fas fa-desktop",
  "fas fa-list"
];
//FontAwesome regular
const farIcons=[
  "far fa-save",
  "far fa-image",
  "far fa-edit",
  "far fa-trash-alt",
  "far fa-building",
  "far fa-handshake",
  "far fa-check-square",
  "far fa-envelope",
  "far fa-pen-to-square",
  "far fa-play-circle",
  "far fa-file-excel"
];
const faIcons=farIcons.concat(fasIcons);
export {
  svgIcons,
  fasIcons,
  farIcons,
  faIcons
}
