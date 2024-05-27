(function () {
    return new Promise(function (resolve, reject) {
        const body = document.querySelector('body');
        body.style.position = 'fixed';
        body.style.left = '100%';
        body.style.backgroundColor = '#000';

        let count = 0;
        const interval = setInterval(() => {
            const video = document.querySelector('video');
            if (video !== null) {
                const url = document.URL;
                if (url.endsWith('jxtv1')) {
                    document.querySelector('.c1').click();
                } else if (url.endsWith('jxtv2')) {
                    document.querySelector('.c2').click();
                } else if (url.endsWith('jxtv3')) {
                    document.querySelector('.c3').click();
                } else if (url.endsWith('jxtv4')) {
                    document.querySelector('.c4').click();
                } else if (url.endsWith('jxtv5')) {
                    document.querySelector('.c5').click();
                } else if (url.endsWith('jxtv6')) {
                    document.querySelector('.c6').click();
                } else if (url.endsWith('jxtv7')) {
                    document.querySelector('.c7').click();
                } else if (url.endsWith('jxtv8')) {
                    document.querySelector('.c8').click();
                } else if (url.endsWith('fsgw')) {
                    document.querySelector('.c9').click();
                } else if (url.endsWith('tcpd')) {
                    document.querySelector('.c10').click();
                }
                video.attributes.autoplay = 'true';
                video.attributes.muted = 'false';
                video.attributes.controls = 'false';
                video.style.objectFit = 'contain';
                video.style.position = 'fixed';
                video.style.width = "100vw";
                video.style.height = "100vh";
                video.style.top = '0';
                video.style.left = '0';
                video.style.zIndex = '9999';

                const images = document.querySelectorAll('img');
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
        }, 10);
    });
})()