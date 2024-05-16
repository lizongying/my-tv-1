(function () {
    return new Promise(function (resolve, reject) {
        const body = document.querySelector('body');
        body.style.position = 'fixed';
        body.style.left = '100%';
        body.style.backgroundColor = '#000';

        const interval = setInterval(() => {
            const video = document.querySelector('video');
            if (video !== null) {
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
                }, 500)
            }
        }, 10);
    });
})()