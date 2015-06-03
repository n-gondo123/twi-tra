window.TwiTra = {}

###
# ゼロサプレス
###
String.prototype.zeroSuppress = (num) ->
  #TODO: ２桁までしか対応していない
  if @.length == num
    @
  else
    @.replace(/(\d)/, "0$1")

Vue.use(window["vue-validator"])

###
# タイムスタンプ変換用フィルター
###
Vue.filter 'fmtDt', (value) ->
  dt = new Date(value)
  [
    dt.getFullYear()
    '/'
    String(dt.getMonth() + 1).zeroSuppress(2)
    '/'
    String(dt.getDate()).zeroSuppress(2)
    ' '
    String(dt.getHours()).zeroSuppress(2)
    ':'
    String(dt.getMinutes()).zeroSuppress(2)
    ':'
    String(dt.getSeconds()).zeroSuppress(2)
  ].join('')

###
# v-repeat用フィルター
# $dataにlimitを定義しておく必要がある
###
Vue.filter 'limit', (array) ->
  array.filter (val, idx) =>
    idx < Number(@.limit) || 0

###
# 表示用フィルター
# strとカンマ区切りで表示
###
Vue.filter 'tail', (array, str) ->
  max = array.length - 1
  array.map (val, idx) ->
    if idx is max
      val + str + ' '
    else
      val + str + ', '
  .join ''


$ ->
  ###
  # ルートVM
  ###
  TwiTra.vueRoot = new Vue()

  TwiTra.vueRoot.$addChild
    el: '#navi'
    data:
      pathname: location.pathname
    methods:
      onNewTweet: (rootId) ->
        TwiTra.vueRoot.$broadcast('showTweetFrom', rootId)
