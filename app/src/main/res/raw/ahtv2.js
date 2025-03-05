(() => {
    document.querySelector('meta[name="viewport"]').content = "width=device-width, initial-scale=1.0"

    const body = document.body;
    body.style.position = 'fixed';
    body.style.left = '100vw';
    body.style.backgroundColor = '#000';

    let timeout = 0;

    const videoStyle = (video) => {
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
        video.style.transform = 'translate(0, 0)';
    };

    const success = (video) => {
        if (video.readyState >= 3) {
            video.play();
        } else {
            video.addEventListener('canplaythrough', () => {
                video.play();
            });
            video.addEventListener('canplaythrough', () => {
                video.play();
            }, {once: true});
        }

        videoStyle(video);

        setTimeout(() => {
            videoStyle(video);
        }, 10);

        setTimeout(() => {
            videoStyle(video);
        }, 100);

        setTimeout(() => {
            videoStyle(video);
        }, 1000);

        console.log('success');
        if (timeout > 0) {
            clearInterval(timeout);
        }
    };

    const observerVideo = (box) => {
        const video = box.querySelector('video');
        if (video !== null) {
            success(video);
            return null
        } else {
            const observer = new MutationObserver((_) => {
                const video = box.querySelector('video');
                if (video !== null) {
                    if (observer !== null) {
                        observer.disconnect();
                    }
                    success(video);
                }
            });

            observer.observe(body, {
                childList: true,
                subtree: true
            });
            return observer
        }
    }

    const observer = observerVideo(body);

    timeout = setTimeout(() => {
        if (observer !== null) {
            observer.disconnect();
        }
        console.log('timeout');
    }, 10000);
})()