import React from 'react';
import './App.css';

import {BrowserRouter as Router, Route, Switch} from "react-router-dom";

import PrivateRoute from "./components/common/PrivateRoute";
import {CSSTransition, TransitionGroup,} from 'react-transition-group';

import {toast, ToastContainer} from 'react-toastify';

import {HomePage} from './components/Home';
import NotFound from "./components/NotFound";
import {LoginPage, SignUpPage} from "./components/login/LoginPage";
import OAuth2RedirectHandler from "./components/login/OAuth2RedirectHandler";
import Dashboard from "./components/dashboard/Dashboard";
import {getCurrentUser} from "./ApiUtil";
import {ACCESS_TOKEN} from "./constants";
import LoadingIndicator from "./components/common/Loading";

import 'react-toastify/dist/ReactToastify.css';

class App extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      authenticated: false,
      currentUser: null,
      loading: false
    };

    this.loadCurrentlyLoggedInUser = this.loadCurrentlyLoggedInUser.bind(this);
    this.handleLogout = this.handleLogout.bind(this);
  }

  componentDidMount() {
    this.loadCurrentlyLoggedInUser()
  }

  loadCurrentlyLoggedInUser() {
    this.setState({loading: true});
    getCurrentUser()
      .then(response => {
        this.setState({
          currentUser: response,
          authenticated: true,
          loading: false
        });
      })
      .catch(error => {
        this.setState({
          loading: false
        });
      });
  }

  handleLogout() {
    localStorage.removeItem(ACCESS_TOKEN);
    this.setState({
      authenticated: false,
      currentUser: null
    });
    toast.success("You're safely logged out!");
  }

  render() {
    if (this.state.loading === true) {
      return <LoadingIndicator/>
    }
    // TODO because you have already added transitions make them work.
    return (
      <>
        <ToastContainer/>
        <Router>
          <Route render={({location}) => (
            <div className={"site"}>
              <TransitionGroup className="transition-group">
                <CSSTransition
                  key={location.pathname}
                  timeout={100}
                  classNames="fade"
                >
                  <div className="route-section" style={{
                    display: "flex",
                    minHeight: "100vh",
                    flexDirection: "column",
                  }}>
                    <main className={"site-content"}>
                      <Switch location={location}>
                        <Route exact path="/" component={HomePage}/>
                        <Route path="/login" render={
                          (props) => <LoginPage authenticated={this.state.authenticated} {...props} />
                        }/>
                        <Route path="/signup" render={
                          (props) => <SignUpPage authenticated={this.state.authenticated} {...props} />
                        }/>
                        <Route path="/oauth2/redirect" component={OAuth2RedirectHandler}/>
                        <PrivateRoute path="/dashboard"
                                      authenticated={this.state.authenticated}
                                      currentUser={this.state.currentUser}
                                      component={Dashboard}/>
                        <Route path="*" component={NotFound}/>
                      </Switch>
                    </main>
                  </div>
                </CSSTransition>
              </TransitionGroup>
            </div>
          )}/>
        </Router>

      </>
    );

  }
}

export default App;
