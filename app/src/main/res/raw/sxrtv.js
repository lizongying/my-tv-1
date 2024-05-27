(function () {
    return new Promise(function (resolve, reject) {
        const body = document.querySelector('body');
        body.style.position = 'fixed';
        body.style.left = '100%';
        body.style.backgroundColor = '#000';

        let count = 0;
        const interval = setInterval(() => {
            const box = body.querySelector('#showplayerbox');
            if (box === null) {
                return
            }

            const video = box.shadowRoot.querySelector('video');
            if (video !== null) {
                box.style.position = 'fixed';
                box.style.width = "100vw";
                box.style.height = "100vh";
                box.style.top = '0';
                box.style.left = '0';
                box.style.zIndex = '9998';

                const url = document.URL;
                const a = url.split('#')
                const items = body.querySelectorAll('#channelstab>li');
                items[parseInt(a[a.length - 1])].click();

                clearInterval(interval);
                setTimeout(function () {
                    console.log('success');
                }, 0)
            }
            count ++;
            if (count > 6 * 1000) {
                clearInterval(interval);
                console.log('timeout');
            }
        }, 10);
    });
})()