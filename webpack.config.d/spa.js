config.devServer = {
    ...config.devServer,
    historyApiFallback: true,
};
config.output = {
    ...config.output,
    publicPath: '/',
};
