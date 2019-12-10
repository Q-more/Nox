import React, {Component} from 'react';

import {getMovies, likeMovie, unlikeMovie} from "../../ApiUtil"

import {
  AppBar,
  Button,
  Card,
  CardActions,
  CardContent,
  CardMedia,
  Container,
  fade,
  Grid,
  IconButton,
  makeStyles,
  Toolbar,
  Typography
} from "@material-ui/core";

import {toast} from "react-toastify"

import FavoriteIcon from '@material-ui/icons/Favorite';

function MovieCard(props) {
  const addToFavorites = () => {
    likeMovie(props.movieDBid)
      .then(() => {
        toast.success(`Added ${props.title} to favorites!`)
      })
      .catch(() => {
        toast.error(`Could not like ${props.title}.`)
      })
  };

  const removeFromFavorites = () => {
    unlikeMovie(props.movieDBid)
      .then(() => {
        toast.success(`Unliked ${props.title}`)
      })
  };

  return (
    <>
      <Card>
        <CardMedia
          component="img"
          alt={props.title}
          height="140"
          image={props.imageURL}
          title={props.title}
        />
        <CardContent>
          <Typography gutterBottom variant="h5" component="h2">
            {props.title}
          </Typography>
          <Typography variant="body2" color="textSecondary" component="p">
            IMDB: {props.score && props.score.imdb} <br/>
            Rotten tomatos: {props.score && props.score.rotten} <br/>
            Meta score: {props.score && props.score.metascore}
          </Typography>
        </CardContent>
        <CardActions>
          <IconButton
            aria-label={`info about ${props.title}`}
            onClick={addToFavorites}
          >
            <FavoriteIcon color={"secondary"}/>
          </IconButton>
        </CardActions>
      </Card>
    </>
  );
}

const navBarStyles = makeStyles(theme => ({
  root: {
    flexGrow: 1,
  },
  menuButton: {
    marginRight: theme.spacing(2),
  },
  title: {
    flexGrow: 1,
    display: 'none',
    [theme.breakpoints.up('sm')]: {
      display: 'block',
    },
  },
  search: {
    position: 'relative',
    borderRadius: theme.shape.borderRadius,
    backgroundColor: fade(theme.palette.common.white, 0.15),
    '&:hover': {
      backgroundColor: fade(theme.palette.common.white, 0.25),
    },
    marginLeft: 0,
    width: '100%',
    [theme.breakpoints.up('sm')]: {
      marginLeft: theme.spacing(1),
      width: 'auto',
    },
  },
  searchIcon: {
    width: theme.spacing(7),
    height: '100%',
    position: 'absolute',
    pointerEvents: 'none',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  inputRoot: {
    color: 'inherit',
  },
  inputInput: {
    padding: theme.spacing(1, 1, 1, 7),
    transition: theme.transitions.create('width'),
    width: '100%',
    [theme.breakpoints.up('sm')]: {
      width: 120,
      '&:focus': {
        width: 200,
      },
    },
  },
}));

function SearchAppBar({movieSearchCallback}) {
  const classes = navBarStyles();

  return (
    <div className={classes.root}>
      <AppBar position="static">
        <Toolbar>
          <Typography className={classes.title} variant="h6" noWrap>
            Nox
          </Typography>
          <Button color={"secondary"} onClick={movieSearchCallback("popular")}>
            Popular
          </Button>
          <Button color={"secondary"} onClick={movieSearchCallback("new-releases")}>
            New Releases
          </Button>
          <Button color={"secondary"} onClick={movieSearchCallback("upcoming")}>
            Upcoming
          </Button>
          <Button color={"secondary"} onClick={movieSearchCallback("weather/zagreb")}>
            Weather
          </Button>
          <Button color={"secondary"} onClick={movieSearchCallback("liked")}>
            Liked
          </Button>
        </Toolbar>
      </AppBar>
    </div>
  );
}

const MoviesGrid = ({movies}) => {
  return (
    <Grid container spacing={4}>
      {movies && movies.map((movie) => (
        <Grid item
              xs={12} sm={6} md={4}
              lg={4} xg={3}
              key={movie.imageURL}>
          <MovieCard {...movie}/>
        </Grid>
      ))
      }
    </Grid>
  )
};

class Dashboard extends Component {

  state = {
    movies: null
  };

  constructor(props) {
    super(props);
  }

  componentDidMount() {
    getMovies("popular")
      .then(movies => {
        this.setState({movies: movies})
      })
  }

  render() {
    return (
      <>
        <SearchAppBar movieSearchCallback={(type) => {
          return () => getMovies(type).then(movies => this.setState({movies}))
        }}/>
        <Container style={{marginTop: "20px"}}>
          <MoviesGrid movies={this.state.movies}/>
        </Container>
      </>
    );
  }
}

export default Dashboard