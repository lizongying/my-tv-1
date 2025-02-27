(() => {
    const body = document.body;
    body.style.position = 'fixed';
    body.style.left = '100vw';
    body.style.backgroundColor = '#000';

    const arr = document.URL.split('#');
    const items = body.querySelectorAll('[onclick="liveList.selectChannel(this)"]');
    items[parseInt(arr[arr.length - 1])].click();

    let timeout = 0

    const success = (video) => {
        console.log('video find');

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

        console.log('success');
        if (timeout > 0) {
            clearInterval(timeout);
        }
    };

    const observerVideo = (box) => {
        const video = box.querySelector('video');
        if (video !== null) {
            success(video);
        } else {
            const observer = new MutationObserver((_) => {
                const video = box.querySelector('video');
                if (video !== null) {
                    observer.disconnect();
                    success(video);
                }
            });

            observer.observe(box, {
                childList: true,
                subtree: true
            });

            return observer
        }
    };

    const observerBox = () => {
        const box = body.querySelector('#showplayerbox');
        if (box !== null) {
            return observerVideo(box.shadowRoot);
        } else {
            const observer = new MutationObserver((_) => {
                const box = body.querySelector('#showplayerbox');
                if (box !== null) {
                    observer.disconnect();
                    return observerVideo(box.shadowRoot);
                }
            });

            observer.observe(body, {
                childList: true,
                subtree: true
            });

            return observer
        }
    }

    const observer = observerBox()

    timeout = setTimeout(() => {
        if (observer !== null) {
            observer.disconnect();
        }
        console.log('timeout');
    }, 10000);
})()