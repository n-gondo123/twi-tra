$ ->
  ###
  # ツイート一覧を取得
  ###
  getTweets = (url, callback) ->
    $.ajax
      url: url
      type: 'GET'
      contentType: 'application/json; charset=UTF-8'
      dataType: 'json'
    .done (response) ->
      callback(response)
    .fail (response, status) ->
      alert('failed.')

    # 種別
  path = location.pathname
  url = undefined
  if path == '/home'
    url = '/json/tweet/list/all' 
  else if path.indexOf('/relation/') != -1
    url = "/json/tweet/relation/#{path.substr('/tweet/relation/'.length)}"
  else
    url = "/json/tweet/list/#{location.pathname.substr('/tweet/list/'.length)}"

  ###
  # ツイート一覧を取得
  ###
  tweetListVm = TwiTra.vueRoot.$addChild
    el: '#tweet-list'
    data:
      tweets: []
      limit: 20
    created: ->
      @.$on 'reloadTweets', ->
        getTweets url, (response) =>
          @.tweets = response
    ready: ->
      @.$emit('reloadTweets')
    methods:
      onReply: (tweet) ->
        window.TwiTra.vueRoot.$broadcast('showTweetForm', tweet.rootId || tweet.id)

      showRelation: (rootId) ->
        location.href = '/tweet/relation/' + rootId if rootId != 0

      scroll: (e) ->
        # TODO: 縮小表示だとうまくいかない時がある...
        @.limit += 20 if (e.target.scrollTop + e.target.offsetHeight) >= e.target.scrollHeight
