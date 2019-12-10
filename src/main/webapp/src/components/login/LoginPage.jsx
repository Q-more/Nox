import React, {Component} from 'react';

import {ACCESS_TOKEN, FACEBOOK_AUTH_URL} from '../../constants';

import {Link, Redirect} from 'react-router-dom';
import {toast} from 'react-toastify';

import {
  Avatar,
  Box,
  Button,
  Container,
  CssBaseline,
  Divider,
  Grid,
  TextField,
  Typography,
  withStyles
} from '@material-ui/core'

import LockOutlinedIcon from '@material-ui/icons/LockOutlined';

import {login, signup} from "../../ApiUtil";
import FacebookIcon from '@material-ui/icons/Facebook';
import "./login.css"

function SocialLogin({msg}) {
  return (
    <div className="social-login" style={{
      margin: "5px",
      width: "100%"
    }}>
      <a href={FACEBOOK_AUTH_URL} style={{textDecoration: "none"}}>
        <Button
          fullWidth={true}
          variant="contained"
          startIcon={<FacebookIcon/>}
        >
          {msg}
        </Button>
      </a>
    </div>
  );
}

function Copyright() {
  return (
    <Typography variant="body2" color="textSecondary" align="center">
      {'Copyright © '}
      <Link color="inherit" to="https://nox.io/">
        Nox.io
      </Link>{' '}
      {new Date().getFullYear()}
      {'.'}
    </Typography>
  );
}

const styles = theme => ({
  paper: {
    marginTop: theme.spacing(8),
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
  },
  avatar: {
    margin: theme.spacing(1),
    backgroundColor: theme.palette.secondary.main,
  },
  form: {
    width: '100%', // Fix IE 11 issue.
    marginTop: theme.spacing(1),
  },
  submit: {
    margin: theme.spacing(3, 0, 2),
  },
  formGrid: {
    marginTop: theme.spacing(1),
  },
  hr: {
    width: "100%",
    margin: theme.spacing(2)
  }
});

class LoginForm extends Component {

  constructor(props) {
    super(props);
    this.state = {
      email: '',
      password: ''
    };
    this.handleInputChange = this.handleInputChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleInputChange(event) {
    const target = event.target;
    const inputName = target.name;
    const inputValue = target.value;

    this.setState({
      [inputName]: inputValue
    });
  }

  handleSubmit(event) {
    event.preventDefault();
    const loginRequest = Object.assign({}, this.state);
    login(loginRequest)
      .then(response => {
        localStorage.setItem(ACCESS_TOKEN, response.accessToken);
        toast.success("You're successfully logged in!");
        this.props.history.push("/");
      }).catch(error => {
      toast.error((error && error.message) || 'Oops! Something went wrong. Please try again!');
    });
  }

  render() {
    const {classes} = this.props;
    return (
      <form className={classes.form} noValidate onSubmit={this.handleSubmit}>
        <TextField
          variant="outlined"
          margin="normal"
          required
          fullWidth
          id="email"
          label="Email Address"
          name="email"
          autoComplete="email"
          autoFocus
          onChange={this.handleInputChange}
        />
        <TextField
          variant="outlined"
          margin="normal"
          required
          fullWidth
          name="password"
          label="Password"
          type="password"
          id="password"
          autoComplete="current-password"
          onChange={this.handleInputChange}
        />
        <Button
          type="submit"
          fullWidth
          variant="contained"
          color="primary"
          className={classes.submit}
        >
          Sign In
        </Button>
        <Grid container className={classes.formGrid}>
          <Grid item xs>
            <Link to="/reset-password" variant="body2">
              Forgot password? TODD!!
            </Link>
          </Grid>
          <Grid item>
            <Link to="/signup" variant="body2">
              {"Don't have an account? Sign Up"}
            </Link>
          </Grid>
        </Grid>
      </form>
    );
  }
}

class LoginPage extends Component {

  componentDidMount() {
    // If the OAuth2 login encounters an error, the user is redirected to the /login page with an error.
    // Here we display the error and then remove the error query parameter from the location.
    if (this.props.location.state && this.props.location.state.error) {
      setTimeout(() => {
        toast.error(this.props.location.state.error);
        this.props.history.replace({
          pathname: this.props.location.pathname,
          state: {}
        });
      }, 100);
    }
  }

  render() {
    if (this.props.authenticated) {

      return <Redirect to={{
        pathname: "/dashboard",
        state: {from: this.props.location}
      }}
      />;
    }
    const {classes} = this.props;
    return (
      <>
        <Container component="main" maxWidth="xs">
          <CssBaseline/>
          <div className={classes.paper}>
            <Avatar className={classes.avatar}>
              <LockOutlinedIcon/>
            </Avatar>
            <Typography component="h1" variant="h5">
              Login to Nox
            </Typography>
            <SocialLogin msg="Login with facebook"/>
            <Divider className={classes.hr}/>
            <LoginForm {...this.props} />
          </div>
          <Box mt={8}>
            <Copyright/>
          </Box>
        </Container>
      </>
    );
  }
}

class SignUpForm extends Component {

  constructor(props) {
    super(props);
    this.state = {
      name: '',
      email: '',
      password: ''
    };
    this.handleInputChange = this.handleInputChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleInputChange(event) {
    const target = event.target;
    const inputName = target.name;
    const inputValue = target.value;

    this.setState({
      [inputName] : inputValue
    });
  }

  handleSubmit(event) {
    event.preventDefault();

    const signUpRequest = Object.assign({}, this.state);
    signup(signUpRequest)
      .then(() => {
        //TODO make this toast
        console.log("You're successfully registered. Please login to continue!");
        this.props.history.push("/signup");
      }).catch(error => {
        console.log("Error while signup");
        console.log((error && error.message) || 'Oops! Something went wrong. Please try again!');
    });
  }

  render() {
    const {classes}  = this.props;
    return (
      <form id="signup-for" className={classes.form} onSubmit={this.handleSubmit} noValidate>
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <TextField
              autoComplete="name"
              name="name"
              variant="outlined"
              required
              fullWidth
              id="name"
              label="Name"
              autoFocus
              onChange={this.handleInputChange}
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              variant="outlined"
              required
              fullWidth
              id="email"
              label="Email Address"
              name="email"
              autoComplete="email"
              onChange={this.handleInputChange}
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              variant="outlined"
              required
              fullWidth
              name="password"
              label="Password"
              type="password"
              id="password"
              autoComplete="current-password"
              onChange={this.handleInputChange}
            />
          </Grid>
        </Grid>
        <Button
          type="submit"
          fullWidth
          variant="contained"
          color="primary"
          className={classes.submit}
        >
          Sign Up
        </Button>
        <Grid container justify="flex-end">
          <Grid item>
            <Link to="/login" variant="body2">
              Already have an account? Sign in
            </Link>
          </Grid>
        </Grid>
      </form>
    );
  }

}

class SignUpPage extends Component {

  render() {
    if (this.props.authenticated) {
      return <Redirect
        to={{
          pathname: "/dashboard",
          state: {from: this.props.location}
        }}/>;
    }

    const {classes} = this.props;
    return (
      <Container component="main" maxWidth="xs">
        <CssBaseline/>
        <div className={classes.paper}>
          <Avatar className={classes.avatar}>
            <LockOutlinedIcon/>
          </Avatar>
          <Typography component="h1" variant="h5">
            Sign up
          </Typography>
          <SocialLogin msg="Sign up with facebook"/>
          <Divider className={classes.hr}/>
          <SignUpForm {...this.props} />
        </div>
        <Box mt={5}>
          <Copyright/>
        </Box>
      </Container>
    );

  }
}

LoginPage = withStyles(styles)(LoginPage);
SignUpPage = withStyles(styles)(SignUpPage);
export {
  LoginPage,
  SignUpPage
};