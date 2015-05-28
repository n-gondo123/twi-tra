$(function() {
    /**
     * ツイート一覧を取得
     */
    var tweetVm = TwiTra.vueRoot.$addChild({
        el: '#tweet-form',
        data: {
            content : '',
            disabled: true
        },
        created: function() {
            /**
             * 空文字ツイート防止
             */
            this.$watch('content', function(value) {
                this.disabled = (value.replace(/[ 　\r\n]/g, '').length === 0)
            });
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
                    TwiTra.vueRoot.$broadcast('reloadTweets');
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
        
    $('#myModal')
        .on('show.bs.modal', function(e) {
            setTimeout(function() {
                $('#tweet-input').focus();
            }, 0);
        })
        .on('hide.bs.modal', function(e) {
            tweetVm.content = '';
        });
});
