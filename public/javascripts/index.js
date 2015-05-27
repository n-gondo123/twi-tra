/**
 * ゼロサプレス
 */
String.prototype.zeroSuppress = function(num) {
    //TODO: ２桁までしか対応していない
    if (this.length === num) {
        return this;
    } else {
        return this.replace(/(\d)/, "0$1");
    }
}

/**
 * タイムスタンプ変換用フィルター
 */
Vue.filter('fmtDt', function(value) {
    var dt = new Date(value);
    return [
        dt.getFullYear(),
        '/',
        String(dt.getMonth() + 1).zeroSuppress(2),
        '/',
        String(dt.getDate()).zeroSuppress(2),
        ' ',
        String(dt.getHours()).zeroSuppress(2),
        ':',
        String(dt.getMinutes()).zeroSuppress(2),
        ':',
        String(dt.getSeconds()).zeroSuppress(2)
    ].join('');
});

$(function() {
    $('input:first').focus().select();
});
