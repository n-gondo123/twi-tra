$ ->
  signFormVm = TwiTra.vueRoot.$addChild
    el: '#sign-form'
    data:
      name: ''
      password: ''
    ready: ->
      $('input:first').focus().select()
