$(function() {
    new Vue({
        el: '#tweet-form',
        data: {
            form: {
                content : ''
            }
        },
        methods: {
            onClick: function (e) {
                var data = {
                    content: this.form.content
                };

                $.ajax({
                    // url: '/json/tweet/create',
                    url: '/json/tweet/create',
                    type: 'POST',
                    // async: false,
                    contentType: 'application/json; charset=UTF-8',
                    dataType: 'json',
                    data: JSON.stringify(data)
                }).done(function (response){
                    alert('success.');
                }).fail(function () {
                    alert('failed.');
                })
            }
        }
    }); 
});
