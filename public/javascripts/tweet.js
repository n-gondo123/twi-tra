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
            allTweets: [],
            dispLimit: 20,
            disabled: true
        },
        created: function() {
            var that = this;
            getTweets('all', function(response) {
                that.allTweets = response;
                that.tweets = response.filter(function(val, idx) {
                    return idx < that.dispLimit;
                })
            });
        },
        methods: {
            scroll: function (e) {
                // TODO: 縮小表示だとうまくいかない...
                var that = this;
                if ((e.target.scrollTop + e.target.offsetHeight) >= e.target.scrollHeight) {
                    that.dispLimit += 20;
                    this.tweets = this.allTweets.filter(function(val, idx) {
                        return idx < that.dispLimit;
                    })
                }
            },

            onShow: function(e) {
                setTimeout(function() {
                    $('#tweet-input').focus();
                }, 0);
            },
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
                    getTweets('all', function(response) {
                        that.dispLimit = 20;
                        that.allTweets = response;
                        that.tweets = response.filter(function(val, idx) {
                            return idx < that.dispLimit;
                        })
                    })
                }).fail(function () {
                    alert('failed.');
                });
            },
            onClear: function(e) {
                if (this.content.length === 0) {
                    return;
                }

                if (confirm('入力内容をクリアします。よろしいですか？')) {
                    this.content = '';
                    $('#tweet-input').focus();
                }
            },
            onCancel: function(e) {
                this.content = '';
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
