(function () {
    return new Promise(function (resolve, reject) {
        const body = document.querySelector('body');
//        body.style.position = 'fixed';
//        body.style.left = '1000%';
//        body.style.backgroundColor = '#000';

                const divElement = document.createElement('div');
                divElement.style.position = 'fixed';
                divElement.style.top = '0';
                divElement.style.left = '0';
                divElement.style.width = '100%';
                divElement.style.height = '100%';
                divElement.style.backgroundColor = '#000';
                divElement.style.zIndex = '9998';
                document.body.appendChild(divElement);

        let count = 0;
        const interval = setInterval(() => {
            const video = body.querySelector('video');
            if (video !== null) {
                const url = document.URL;
                const a = url.split('#');
                const items = body.querySelectorAll('.channel-list li');
                console.log(222222,items.length, items[parseInt(a[a.length - 1])]);
//                items[parseInt(a[a.length - 1])].click();
//                console.log(33333,click);
                video.attributes.autoplay = 'true';
                video.attributes.muted = 'false';
                video.attributes.controls = 'false';
                video.style.objectFit = 'contain';
                video.style.position = 'fixed';
                video.style.width = window.outerHeight*16/9 +'px';
                video.style.height = window.outerHeight +'px';
                video.style.top = '0';
                video.style.left = '0';
                video.style.zIndex = '9999';

                const images = body.querySelectorAll('img');
                for(let i = 0; i < images.length; i++) {
                    images[i].style.display = 'none';
                }
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
        }, 100);
    });
})()