(function () {
    const channel = '{channel}';
    const host = window.location.host;
    switch (host) {
        case 'tv.cctv.com': {
            localStorage.setItem('cctv_live_resolution', '720');
        }
    }
    const originalSend = XMLHttpRequest.prototype.send;
    const originalOpen = XMLHttpRequest.prototype.open;

    XMLHttpRequest.prototype.open = function(method, url, async, user, password) {
        this._url = url;  // Store URL for later use
        return originalOpen.apply(this, arguments);
    };

    XMLHttpRequest.prototype.send = function(body) {
        this.addEventListener('load', function() {
            console.log('URL:', this._url);
            let response;

            if (this.responseType === '' || this.responseType === 'text') {
                response = this.responseText;
            } else {
                response = JSON.stringify(this.response);
            }
            console.log(`channel {channel}, Response:`, response);
        });
        return originalSend.apply(this, arguments);
    };
})()