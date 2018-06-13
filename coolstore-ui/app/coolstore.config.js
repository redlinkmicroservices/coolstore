var config =
{
  API_ENDPOINT: (process.env.COOLSTORE_GW_ENDPOINT != null ? process.env.COOLSTORE_GW_ENDPOINT : ''),
  SSO_ENABLED: process.env.SSO_URL ? true : false
};

module.exports = config;
