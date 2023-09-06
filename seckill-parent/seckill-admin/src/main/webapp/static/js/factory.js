var basePath = $("input[name='contextPath']").val();
var sharePath = $("input[name='sharePath']").val();
var activityId = $("input[name='activityId']").val();
var channelId = $("input[name='channelId']").val();
var recommendType = $("input[name='recommendType']").val();

var util = {
    noop:function(){},
    params:function(data, isEncode){
        var encodeURI = isEncode === false ? function(s){ return s } : encodeURIComponent;
        var result = [];
        for(var k in data){
            result.push(k + "=" + encodeURI(data[k]));
        }
        return result.join('&');
    },
    trimAll:function(e){
        return null == e ? "" : e.replace(/\s/g, "");
    },
    isSmsCode:function(value){
        return /^[0-9]{6}$/.test(value);
    },
    isPassword:function(value){
        return /^(?!^[0-9]*$)(?!^[a-zA-Z]*$)^([a-zA-Z0-9]{6,20})$/.test(value);
    },
    isPasswordLength:function(value){
        return /^\S{6,20}$/.test(value);
    },
    isMobile:function(value){
        return /^1[3|4|5|7|8]\d{9}$/.test(value);
    },
    //6位数字或字母
    isNumchart:function(value){
        return /^[a-zA-Z\d]{6}$/.test(value);
    },
    isEmpty:function(value){
        return value == "" || value == null || value == undefined;
    },
    isRealName:function(value){
        var t = util.trimAll(value);
        return t.length < 2 || !/^[\u4e00-\u9fa5]{1,10}$/.test(t) ? false : true;
    },
    isBankCard:function(e){
        return /^[0-9]{15,19}$/.test(e)
    },
    isIdCard:function(Id){
        var pass = !0, regx = /^[1-9][0-9]{5}(19[0-9]{2}|200[0-9]|2010)(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])[0-9]{3}[0-9xX]$/i;
        if (Id && regx.test(Id)) {
            Id = Id.toUpperCase();
            if (city[Id.substr(0, 2)]) {
                if (18 == Id.length) {
                    Id = Id.split("");
                    var factor = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 ];
                    var parity = [ 1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2 ];
                    var sum = 0, ai = 0, wi = 0;
                    for (var i = 0; i < 17; i++) {
                        ai = Id[i];
                        wi = factor[i];
                        sum += ai * wi;
                    }
                    var last = parity[sum % 11];
                    if(parity[sum % 11] != Id[17]){
                        pass =false;
                    }
                }
            } else pass = !1;
        } else pass = !1;
        return pass;
    }
};


weixin = {
    inWeixin: false,
    defaultOptions: {
        shareDesc: "新手加息券，加息加不停！",
        shareTitle: "新手注册送加息券",
        link: sharePath+"/index/invitePage.do",
        imgUrl: sharePath+"/static/images/shareIcon.png"
    }
};
var wxInit = function (accountId) {
    var UA = window.navigator.userAgent.toLowerCase();
    if (UA.match(/MicroMessenger/i) == "micromessenger") {
        weixin.inWeixin = true;
       // https://open.weixin.qq.com/connect/oauth2/authorize?appid=${AppID}&redirect_uri=${WECHAT_URL}&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect
        $.post(basePath + '/wechat/signature.do', {url: window.location.href}).success(function (data) {
            var json = JSON.parse(data);
            var linkUrl = weixin.defaultOptions.link + "?AC_CHANNEL_ID="+channelId+"&AC_ACTIVITY_ID="+activityId+"&RECOMMENDER_TYPE="+recommendType+"&PAGE_TYPE=1&API_VERSISON=1&RECOMMENDER_ID=" + accountId;
           // var linkUrl ="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx7f88eb81e292b4d9&redirect_uri=" + encodeURIComponent(url)+"&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
           // console.log(linkUrl);
            // 验证信息
            wx.config({
                debug: false,
                appId: json.data.info.appid,
                timestamp: json.data.info.timestamp,
                nonceStr: json.data.info.noncestr,
                signature: json.data.info.signature,
                jsApiList: json.data.info.jsApiList
            });
            //   });
            wx.ready(function () {
                // 监听用户“分享到朋友圈”操作
                wx.onMenuShareTimeline({
                    title: weixin.defaultOptions.shareTitle,
                    link: linkUrl,
                    imgUrl: weixin.defaultOptions.imgUrl,
                    desc: weixin.defaultOptions.shareDesc,
                    success: function () {
                    },
                    cancel: function () {
                    }
                });
                // 分享給朋友
                wx.onMenuShareAppMessage({
                    title: weixin.defaultOptions.shareTitle,
                    link: linkUrl,
                    imgUrl: weixin.defaultOptions.imgUrl,
                    desc: weixin.defaultOptions.shareDesc,
                    success: function () {
                    },
                    cancel: function () {
                    }
                });
            });
        })
    }
};


