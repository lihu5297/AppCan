var windowHeight = $(document.body).height() - 60; //浏览器当前窗口可视区域高度
      $(".main .left").css('minHeight', windowHeight);
      $(".main .right .content").css('minHeight', windowHeight-60);
      $(".main .right .right-content").css('minHeight', windowHeight-140);
      $(window).resize(function () {
        var windowHeight = $(document.body).height() - 60; //浏览器当前窗口可视区域高度
        $(".main .left").css('minHeight', windowHeight);
        $(".main .right .content").css('minHeight', windowHeight-60);
        $(".main .right .right-content").css('minHeight', windowHeight-140);
      });
      $(".menuNav > li > a").click(function(event) {
        $(".menuNav > li .nav-pills").slideUp('slow');
        if ($(this).siblings('.nav-pills').css("display")=="none") {
          $(this).siblings('.nav-pills').slideDown('slow');
        };
      });
      
      
      
/**
 * 如果为null或者是空字符串返回空字符串
 * @param str
 * @returns
 */
  function ifNull(str) {
	if (null == str || "" == str) {
		return "";
	} else {
		return str;
	}
}