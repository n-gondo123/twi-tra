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
        }).done(function (response) {
            callback(response);
        }).fail(function (response, status) {
            alert('failed.');
        });
    };

    /** 種別 */
    var kind = location.pathname === '/home' ? 'all' : location.pathname.substr('/tweet/list/'.length);

    var tweetListVm = TwiTra.vueRoot.$addChild({
        el: '#tweet-list',
        data: {
            tweets: [],
            allTweets: [],
            dispLimit: 50,
        },
        created: function() {
            var that = this;
            that.$on('reloadTweets', function() {
                getTweets(kind, function(response) {
                    that.allTweets = response;
                    that.tweets = response.filter(function(val, idx) {
                        return idx < that.dispLimit;
                    })
                    $('.tree').treegrid();
                });
            });
        },
        ready: function() {
            this.$emit('reloadTweets');
        },
        methods: {
            onReply: function(rootId) {
                TwiTra.vueRoot.$broadcast('showTweetForm', rootId);
            },

            scroll: function (e) {
                // TODO: 縮小表示だとうまくいかない時がある...
                var that = this;
                if ((e.target.scrollTop + e.target.offsetHeight) >= e.target.scrollHeight) {
                    that.dispLimit += 20;
                    this.tweets = this.allTweets.filter(function(val, idx) {
                        return idx < that.dispLimit;
                    })
                }
            }
        }
    });
});
