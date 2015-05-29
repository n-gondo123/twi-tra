$ ->
  userFormVm = TwiTra.vueRoot.$addChild
    el: '#user-form'
    data: 
      name: ''
      email: ''
      password: ''
    ready: ->
      $('input:first').focus().select()
