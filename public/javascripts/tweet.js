$(function() {
    /**
     * ツイート一覧を取得
     */
    var getTweets = function(kind, callback) {
        $.ajax({
            url: '/json/tweet/list/' + kind,
            type: 'GET',
            contentType: 'application/json; charset=UTF-8',
            dataType: 'json',
        }).done(function (response){
            callback(response);
        }).fail(function () {
            alert('failed.');
        })
    };

    var tweetVm = new Vue({
        el: '#tweet',
        data: {
            content : '',
            tweets: [],
            disabled: true
        },
        created: function() {
            var that = this;
            getTweets('all', function(response) {
                that.tweets = response;
            })
        },
        methods: {
            onTweet: function(e) {
                var that = this;
                var data = {
                    content: this.content
                };

                $.ajax({
                    url: '/json/tweet/create',
                    type: 'POST',
                    contentType: 'application/json; charset=UTF-8',
                    dataType: 'json',
                    data: JSON.stringify(data)
                }).done(function (response){
                    that.content = '';
                    $('#tweet-input').focus();
                    getTweets('all', function(response) {
                        that.tweets = response;
                    })
                }).fail(function () {
                    alert('failed.');
                })
            },
            onCancel: function() {
                if (this.content.length === 0) {
                    return;
                }

                if (confirm('入力内容をクリアします。よろしいですか？')) {
                    this.content = '';
                    $('#tweet-input').focus();
                }
            }
        }
    });
    
    /**
     * 空文字ツイート防止
     */
    tweetVm.$watch('content', function(value) {
        this.disabled = (value.replace(/[ 　\r\n]/g, '').length === 0)
    });
});
