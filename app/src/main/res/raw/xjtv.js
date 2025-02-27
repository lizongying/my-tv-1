(function () {
    return new Promise(function (resolve, reject) {
        const body = document.querySelector('body');
//        body.style.position = 'fixed';
//        body.style.left = '100%';
//        body.style.backgroundColor = '#000';

        let count = 0;
        const interval = setInterval(() => {
            let video = body.querySelector('video');
            if (video !== null) {
                const children = body.children;
                for (let i = 0; i < children.length; i++) {
                    children[i].style.display = 'none';
                }

                const url = document.URL;
                const a = url.split('#');
                const items = body.querySelectorAll('.list_name');
                console.log('items', items.length,parseInt(a[a.length - 1]), items[parseInt(a[a.length - 1])]);

    items[parseInt(a[a.length - 1])].click();
                                 console.log('111111',11111111);
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

                                body.appendChild(video);

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