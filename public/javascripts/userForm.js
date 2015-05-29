$(function() {
    var userFormVm = TwiTra.vueRoot.$addChild({
        el: '#user-form',
        data: {
            name: '',
            email: '',
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
