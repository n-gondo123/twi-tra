$(function() {
    var signFormVm = TwiTra.vueRoot.$addChild({
        el: '#sign-form',
        data: {
            name: '',
            password: ''
        },
        created: function() {

        },
        ready: function() {
            $('input:first').focus().select();
        },
        methods: {

        }
    });
});
