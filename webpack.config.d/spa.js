config.devServer = {
    ...config.devServer,
    historyApiFallback: {
        index: '/',
        disableDotRule: true,
    },
};
