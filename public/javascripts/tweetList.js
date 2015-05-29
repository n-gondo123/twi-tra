$(function() {
    /**
     * ツイート一覧を取得
     */
    var getTweets = function(url, callback) {
        $.ajax({
            url: url,
            type: 'GET',
            contentType: 'application/json; charset=UTF-8',
            dataType: 'json',
        }).done(function (response) {
            callback(response);
        }).fail(function (response, status) {
            alert('failed.');
        });
    };

    /** 種別 */
    var url =
        location.pathname === '/home' ? '/json/tweet/list/all'
            : location.pathname.indexOf('/relation/') !== -1 ? '/json/tweet/relation/' + location.pathname.substr('/tweet/relation/'.length)
                : '/json/tweet/list/' + location.pathname.substr('/tweet/list/'.length);

    var tweetListVm = TwiTra.vueRoot.$addChild({
        el: '#tweet-list',
        data: {
            tweets: [],
            // allTweets: [],
            limit: 20
        },
        created: function() {
            var that = this;
            that.$on('reloadTweets', function() {
                getTweets(url, function(response) {
                    that.tweets = response;
                });
            });
        },
        ready: function() {
            this.$emit('reloadTweets');
        },
        methods: {
            onReply: function(tweet) {
                TwiTra.vueRoot.$broadcast('showTweetForm', tweet.rootId || tweet.id);
            },
            showThread: function(rootId) {
                if (rootId !== 0) {
                    location.href = '/tweet/relation/' + rootId;
                }
            },
            scroll: function (e) {
                // TODO: 縮小表示だとうまくいかない時がある...
                var that = this;
                if ((e.target.scrollTop + e.target.offsetHeight) >= e.target.scrollHeight) {
                    that.limit += 20;
                }
            }
        }
    });
});
