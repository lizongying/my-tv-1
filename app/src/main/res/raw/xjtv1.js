(() => {
    const body = document.body;
    body.style.position = 'fixed';
    body.style.left = '100vw';
    body.style.backgroundColor = '#000';

    let timeout = 0;
    let selector = '{selector}';
    let index = {index};

    const success = (video) => {
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

        console.log('success');
        if (timeout > 0) {
            clearInterval(timeout);
        }
    };

    const observerSelector = () => {
        let items = body.querySelectorAll(selector);
        if (items.length > index) {
            items[index].click();
            return null;
        } else {
            const observer = new MutationObserver((_) => {
                items = body.querySelectorAll(selector);
                if (items.length > index) {
                    if (observer !== null) {
                        observer.disconnect();
                    }
                    items[index].click();
                    return null;
                }
            });

            observer.observe(body, {
                childList: true,
                subtree: true,
                attributes: false,
                characterData: false
            });
            return observer
        }
    };

    const observerVideo = (box) => {
        const video = box.querySelector('video');
        if (video !== null) {
            if (index !== 0) {
                setTimeout(() => {
                    observerSelector();
                    success(box.querySelector('video'));
                }, 0);
            } else {
                success(video);
            }
            return null
        } else {
            const observer = new MutationObserver((_) => {
                const video = box.querySelector('video');
                if (video !== null) {
                    if (observer !== null) {
                        observer.disconnect();
                    }

                    if (index !== 0) {
                        setTimeout(() => {
                            observerSelector();
                            success(box.querySelector('video'));
                        }, 0);
                    } else {
                        success(video);
                    }
                }
            });

            observer.observe(box, {
                childList: true,
                subtree: true
            });
            return observer
        }
    };

    const observer = observerVideo(body);

    timeout = setTimeout(() => {
        if (observer !== null) {
            observer.disconnect();
        }
        console.log('timeout');
    }, 10000);
})()