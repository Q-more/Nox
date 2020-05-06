import {ACCESS_TOKEN, API_BASE_URL} from './constants';

const request = ({extractJson = true, ...options}) => {
  const headers = new Headers({
    'Content-Type': 'application/json',
  });

  const accessToken = localStorage.getItem(ACCESS_TOKEN);
  if (accessToken) {
    headers.append('Authorization', 'Bearer ' + accessToken);
  }

  const defaults = {headers: headers};
  options = Object.assign({}, defaults, options);

  return fetch(options.url, options)
    .then(response => {
        if (extractJson) {
          return response.json().then(json => {
            if (!response.ok) {
              return Promise.reject(json);
            }
            return json;
          })
        }
        if (!response.ok) return Promise.reject();
        return response;
      }
    );
};

export function getCurrentUser() {
  if (!localStorage.getItem(ACCESS_TOKEN)) {
    return Promise.reject("No access token set.");
  }
  return request({
    url: API_BASE_URL + "/user/me",
    method: 'GET'
  });
}

export function login(loginRequest) {
  return request({
    url: API_BASE_URL + "/auth/login",
    method: 'POST',
    body: JSON.stringify(loginRequest)
  });
}

export function signup(signupRequest) {
  return request({
    url: API_BASE_URL + "/auth/signup",
    method: 'POST',
    body: JSON.stringify(signupRequest)
  });
}

export function getMovies(type) {
  return request({
    url: API_BASE_URL + "/movies/" + type,
  })
}

export function likeMovie(movieId) {
  return request({
    extractJson: false,
    method: "POST",
    url: API_BASE_URL + "/likes/" + movieId,
  });
}

export function unlikeMovie(movieId) {
  return request({
    extractJson: false,
    method: "POST",
    url: API_BASE_URL + "/likes/" + movieId,
  });
}