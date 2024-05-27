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
            body.appendChild(video);
                const url = document.URL;
                const a = url.split('#')
                const items = document.querySelectorAll('#J_live_list>li>a');
                items[parseInt(a[a.length - 1])].click();
                video.attributes.autoplay = 'true';
                video.attributes.muted = 'false';
                video.attributes.controls = 'false';
                video.style.objectFit = 'contain';
                video.style.display = 'block';
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