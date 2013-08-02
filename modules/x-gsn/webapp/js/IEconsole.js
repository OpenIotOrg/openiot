if (!window.console) {
  window.console = {
    timers: {},
    openwin: function() {
      window.top.debugWindow =
          window.open("",
                      "Debug",
                      "left=0,top=0,width=300,height=700,scrollbars=yes,"
                      +"status=yes,resizable=yes");
      window.top.debugWindow.opener = self;
      window.top.debugWindow.document.open();
      window.top.debugWindow.document.write('<html><head><title>debug window</title></head><body><hr /><pre>');
    },

    debug: function(entry) {
      window.top.debugWindow.document.write(entry+"\n");
    },

    time: function(title) {
      window.console.timers[title] = new Date().getTime();
    },

    timeEnd: function(title) {
      var time = new Date().getTime() - window.console.timers[title];
      console.log(['<strong>', title, '</strong>: ', time, 'ms'].join(''));
    }

  }

  if (!window.top.debugWindow) { console.openwin(); }
}
