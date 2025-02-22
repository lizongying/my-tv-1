(function () {
    const body = document.querySelector('body');
    body.style.position = 'fixed';
    body.style.left = '100vw';
    body.style.backgroundColor = '#000';

    let count = 0;
    const interval = setInterval(() => {
        const video = body.querySelector('video');
        if (video !== null) {
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

            const images = body.querySelectorAll('img');
            for (let i = 0; i < images.length; i++) {
                images[i].style.display = 'none';
            }
            clearInterval(interval);
            setTimeout(function () {
                console.log('success');
            }, 0)
        }
        count++;
        if (count > 6 * 1000) {
            clearInterval(interval);
            console.log('timeout');
        }
    }, 10);
})()