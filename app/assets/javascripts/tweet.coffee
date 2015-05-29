$ ->
  ###
  # ツイート一フォームVM
  ###
  tweetVm = TwiTra.vueRoot.$addChild
    el: '#tweet-form'
    data:
      content : ''
      rootId: 0
      disabled: true

    created: -> 
      ###
      # 空文字ツイート防止
      ###
      @.$watch 'content', (value) ->
        @.disabled = value.replace(/[ 　\r\n]/g, '').length == 0

      @.$on 'showTweetForm', (rootId) ->
        @.rootId = rootId

    methods:
      onTweet: (e) ->
        data =
          content: @.content
          rootId: @.rootId

        $.ajax
          url: '/json/tweet/create'
          type: 'POST'
          contentType: 'application/json; charset=UTF-8'
          dataType: 'json'
          data: JSON.stringify(data)
        .done (response) ->
          TwiTra.vueRoot.$broadcast('reloadTweets')
        .fail ->
          alert('failed.')

      onClear: (e) ->
        return if (@.content.length == 0)
        if confirm('入力内容をクリアします。よろしいですか？')
          @.content = ''
          $('#tweet-input').focus()

      onCancel: (e) ->
        @.content = ''

  $('#myModal')
  .on 'show.bs.modal', (e) ->
    setTimeout ->
      $('#tweet-input').focus()
    , 0
  .on 'hide.bs.modal', (e) ->
    tweetVm.content = ''
