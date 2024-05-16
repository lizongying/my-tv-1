(function () {
    return new Promise(function (resolve, reject) {
        const body = document.querySelector('body');
        body.style.position = 'fixed';
        body.style.left = '100%';
        body.style.backgroundColor = '#000';
//        const divElement = document.createElement('div');
//        divElement.style.position = 'fixed';
//        divElement.style.top = '0';
//        divElement.style.left = '0';
//        divElement.style.width = '100%';
//        divElement.style.height = '100%';
//        divElement.style.backgroundColor = '#000';
//        divElement.style.zIndex = '9998';
//        document.body.appendChild(divElement);

        const height = document.documentElement.clientHeight;
        const interval = setInterval(() => {
            const video = document.querySelector('video');
            if (video !== null) {
                video.style.objectFit = 'contain';
                video.style.position = 'fixed';
                video.style.width = height * 16 / 9 + 'px';
                video.style.height = height + 'px';
                video.style.top = '0';
                video.style.left = '0';
                video.style.zIndex = '9998';

                const images = document.querySelectorAll('img');
                for(let i = 0; i < images.length; i++) {
                    images[i].style.display = 'none';
                }
                clearInterval(interval);
                setTimeout(function () {
                    console.log('success');
                }, 0)
            }
        }, 10);
    });
})()