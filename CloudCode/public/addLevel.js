var theMovieDb={};theMovieDb.common={api_key:"3194901495f601638f6dee6a2066c032",base_uri:"http://api.themoviedb.org/3/",images_uri:"http://image.tmdb.org/t/p/",timeout:5000,generateQuery:function(b){var a,d,c;a=b||{};d="?api_key="+theMovieDb.common.api_key;if(Object.keys(a).length>0){for(c in a){if(a.hasOwnProperty(c)&&c!=="id"&&c!=="body"){d=d+"&"+c+"="+a[c]}}}return d},validateCallbacks:function(a){if(typeof a[0]!=="function"||typeof a[1]!=="function"){throw"Success and error parameters must be functions!"}},validateRequired:function(b,f,d,e,g){var c,a;a=g||false;if(b.length!==f){throw"The method requires  "+f+" arguments and you are sending "+b.length+"!"}if(a){return}if(f>2){for(c=0;c<e.length;c=c+1){if(!d.hasOwnProperty(e[c])){throw e[c]+" is a required parameter and is not present in the options!"}}}},getImage:function(a){return theMovieDb.common.images_uri+a.size+"/"+a.file},client:function(c,e,b){var f,a,d;f=c.method||"GET";a=c.status||200;d=new XMLHttpRequest();d.ontimeout=function(){b('{"status_code":408,"status_message":"Request timed out"}')};d.open(f,theMovieDb.common.base_uri+c.url,true);if(c.method==="POST"){d.setRequestHeader("Content-Type","application/json");d.setRequestHeader("Accept","application/json")}d.timeout=theMovieDb.common.timeout;d.onload=function(g){if(d.readyState===4){if(d.status===a){e(d.responseText)}else{b(d.responseText)}}else{b(d.responseText)}};d.onerror=function(g){b(d.responseText)};if(c.method==="POST"){d.send(JSON.stringify(c.body))}else{d.send(null)}}};theMovieDb.configurations={getConfiguration:function(b,a){theMovieDb.common.validateRequired(arguments,2);theMovieDb.common.validateCallbacks([b,a]);theMovieDb.common.client({url:"configuration"+theMovieDb.common.generateQuery()},b,a)}};theMovieDb.account={getInformation:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["session_id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"account"+theMovieDb.common.generateQuery(b)},c,a)},getLists:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["session_id","id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"account/"+b.id+"/lists"+theMovieDb.common.generateQuery(b)},c,a)},getFavoritesMovies:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["session_id","id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"account/"+b.id+"/favorite_movies"+theMovieDb.common.generateQuery(b)},c,a)},addFavorite:function(c,d,b){var a;theMovieDb.common.validateRequired(arguments,3,c,["session_id","id","movie_id","favorite"]);theMovieDb.common.validateCallbacks([d,b]);a={movie_id:c.movie_id,favorite:c.favorite};theMovieDb.common.client({url:"account/"+c.id+"/favorite"+theMovieDb.common.generateQuery(c),status:201,method:"POST",body:a},d,b)},getRatedMovies:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["session_id","id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"account/"+b.id+"/rated_movies"+theMovieDb.common.generateQuery(b)},c,a)},getWatchlist:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["session_id","id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"account/"+b.id+"/movie_watchlist"+theMovieDb.common.generateQuery(b)},c,a)},addMovieToWatchlist:function(c,d,b){var a;theMovieDb.common.validateRequired(arguments,3,c,["session_id","id","movie_id","movie_watchlist"]);theMovieDb.common.validateCallbacks([d,b]);a={movie_id:c.movie_id,movie_watchlist:c.movie_watchlist};theMovieDb.common.client({url:"account/"+c.id+"/movie_watchlist"+theMovieDb.common.generateQuery(c),method:"POST",status:201,body:a},d,b)}};theMovieDb.authentication={generateToken:function(b,a){theMovieDb.common.validateRequired(arguments,2);theMovieDb.common.validateCallbacks([b,a]);theMovieDb.common.client({url:"authentication/token/new"+theMovieDb.common.generateQuery()},b,a)},askPermissions:function(a){window.open("https://www.themoviedb.org/authenticate/"+a.token+"?redirect_to="+a.redirect_to)},validateUser:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["request_token","username","password"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"authentication/token/validate_with_login"+theMovieDb.common.generateQuery(b)},c,a)},generateSession:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["request_token"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"authentication/session/new"+theMovieDb.common.generateQuery(b)},c,a)},generateGuestSession:function(b,a){theMovieDb.common.validateRequired(arguments,2);theMovieDb.common.validateCallbacks([b,a]);theMovieDb.common.client({url:"authentication/guest_session/new"+theMovieDb.common.generateQuery()},b,a)}};theMovieDb.certifications={getList:function(b,a){theMovieDb.common.validateRequired(arguments,2);theMovieDb.common.validateCallbacks([b,a]);theMovieDb.common.client({url:"certification/movie/list"+theMovieDb.common.generateQuery()},b,a)}};theMovieDb.changes={getMovieChanges:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,"","",true);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"movie/changes"+theMovieDb.common.generateQuery(b)},c,a)},getPersonChanges:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,"","",true);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"person/changes"+theMovieDb.common.generateQuery(b)},c,a)}};theMovieDb.collections={getCollection:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"collection/"+b.id+theMovieDb.common.generateQuery(b)},c,a)},getCollectionImages:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"collection/"+b.id+"/images"+theMovieDb.common.generateQuery(b)},c,a)}};theMovieDb.companies={getCompany:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"company/"+b.id+theMovieDb.common.generateQuery(b)},c,a)},getCompanyMovies:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"company/"+b.id+"/movies"+theMovieDb.common.generateQuery(b)},c,a)}};theMovieDb.credits={getCredit:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"credit/"+b.id+theMovieDb.common.generateQuery(b)},c,a)}};theMovieDb.discover={getMovies:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,"","",true);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"discover/movie"+theMovieDb.common.generateQuery(b)},c,a)},getTvShows:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,"","",true);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"discover/tv"+theMovieDb.common.generateQuery(b)},c,a)}};theMovieDb.find={getById:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id","external_source"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"find/"+b.id+theMovieDb.common.generateQuery(b)},c,a)}};theMovieDb.genres={getList:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,"","",true);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"genre/list"+theMovieDb.common.generateQuery(b)},c,a)},getMovies:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"genre/"+b.id+"/movies"+theMovieDb.common.generateQuery(b)},c,a)}};theMovieDb.jobs={getList:function(b,a){theMovieDb.common.validateRequired(arguments,2);theMovieDb.common.validateCallbacks([b,a]);theMovieDb.common.client({url:"job/list"+theMovieDb.common.generateQuery()},b,a)}};theMovieDb.keywords={getById:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"keyword/"+b.id+theMovieDb.common.generateQuery(b)},c,a)},getMovies:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"keyword/"+b.id+"/movies"+theMovieDb.common.generateQuery(b)},c,a)}};theMovieDb.lists={getById:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"list/"+b.id+theMovieDb.common.generateQuery(b)},c,a)},getStatusById:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id","movie_id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"list/"+b.id+"/item_status"+theMovieDb.common.generateQuery(b)},c,a)},addList:function(c,d,b){var a;theMovieDb.common.validateRequired(arguments,3,c,["session_id","name","description"]);theMovieDb.common.validateCallbacks([d,b]);a={name:c.name,description:c.description};delete c.name;delete c.description;if(c.hasOwnProperty("language")){a.language=c.language;delete c.language}theMovieDb.common.client({method:"POST",status:201,url:"list"+theMovieDb.common.generateQuery(c),body:a},d,b)},addItem:function(c,d,b){var a;theMovieDb.common.validateRequired(arguments,3,c,["session_id","id","media_id"]);theMovieDb.common.validateCallbacks([d,b]);a={media_id:c.media_id};theMovieDb.common.client({method:"POST",status:201,url:"list/"+c.id+"/add_item"+theMovieDb.common.generateQuery(c),body:a},d,b)},removeItem:function(c,d,b){var a;theMovieDb.common.validateRequired(arguments,3,c,["session_id","id","media_id"]);theMovieDb.common.validateCallbacks([d,b]);a={media_id:c.media_id};theMovieDb.common.client({method:"POST",status:201,url:"list/"+c.id+"/remove_item"+theMovieDb.common.generateQuery(c),body:a},d,b)},removeList:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["session_id","id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({method:"DELETE",status:204,url:"list/"+b.id+theMovieDb.common.generateQuery(b)},c,a)},clearList:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["session_id","id","confirm"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({method:"POST",status:204,body:{},url:"list/"+b.id+"/clear"+theMovieDb.common.generateQuery(b)},c,a)}};theMovieDb.movies={getById:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"movie/"+b.id+theMovieDb.common.generateQuery(b)},c,a)},getAlternativeTitles:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"movie/"+b.id+"/alternative_titles"+theMovieDb.common.generateQuery(b)},c,a)},getCredits:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"movie/"+b.id+"/credits"+theMovieDb.common.generateQuery(b)},c,a)},getImages:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"movie/"+b.id+"/images"+theMovieDb.common.generateQuery(b)},c,a)},getKeywords:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"movie/"+b.id+"/keywords"+theMovieDb.common.generateQuery(b)},c,a)},getReleases:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"movie/"+b.id+"/releases"+theMovieDb.common.generateQuery(b)},c,a)},getTrailers:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"movie/"+b.id+"/trailers"+theMovieDb.common.generateQuery(b)},c,a)},getVideos:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"movie/"+b.id+"/videos"+theMovieDb.common.generateQuery(b)},c,a)},getTranslations:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"movie/"+b.id+"/translations"+theMovieDb.common.generateQuery(b)},c,a)},getSimilarMovies:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"movie/"+b.id+"/similar_movies"+theMovieDb.common.generateQuery(b)},c,a)},getReviews:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"movie/"+b.id+"/reviews"+theMovieDb.common.generateQuery(b)},c,a)},getLists:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"movie/"+b.id+"/lists"+theMovieDb.common.generateQuery(b)},c,a)},getChanges:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"movie/"+b.id+"/changes"+theMovieDb.common.generateQuery(b)},c,a)},getLatest:function(b,a){theMovieDb.common.validateRequired(arguments,2);theMovieDb.common.validateCallbacks([b,a]);theMovieDb.common.client({url:"movie/latest"+theMovieDb.common.generateQuery()},b,a)},getUpcoming:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,"","",true);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"movie/upcoming"+theMovieDb.common.generateQuery(b)},c,a)},getNowPlaying:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,"","",true);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"movie/now_playing"+theMovieDb.common.generateQuery(b)},c,a)},getPopular:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,"","",true);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"movie/popular"+theMovieDb.common.generateQuery(b)},c,a)},getTopRated:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,"","",true);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"movie/top_rated"+theMovieDb.common.generateQuery(b)},c,a)},getStatus:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["session_id","id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"movie/"+b.id+"/account_states"+theMovieDb.common.generateQuery(b)},c,a)},rate:function(b,c,d,a){theMovieDb.common.validateRequired(arguments,4,b,["session_id","id"]);theMovieDb.common.validateCallbacks([d,a]);theMovieDb.common.client({method:"POST",status:201,url:"movie/"+b.id+"/rating"+theMovieDb.common.generateQuery(b),body:{value:c}},d,a)},rateGuest:function(b,c,d,a){theMovieDb.common.validateRequired(arguments,4,b,["guest_session_id","id"]);theMovieDb.common.validateCallbacks([d,a]);theMovieDb.common.client({method:"POST",status:201,url:"movie/"+b.id+"/rating"+theMovieDb.common.generateQuery(b),body:{value:c}},d,a)}};theMovieDb.networks={getById:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"network/"+b.id+theMovieDb.common.generateQuery(b)},c,a)}};theMovieDb.people={getById:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"person/"+b.id+theMovieDb.common.generateQuery(b)},c,a)},getMovieCredits:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"person/"+b.id+"/movie_credits"+theMovieDb.common.generateQuery(b)},c,a)},getTvCredits:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"person/"+b.id+"/tv_credits"+theMovieDb.common.generateQuery(b)},c,a)},getCredits:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"person/"+b.id+"/combined_credits"+theMovieDb.common.generateQuery(b)},c,a)},getExternalIds:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"person/"+b.id+"/external_ids"+theMovieDb.common.generateQuery(b)},c,a)},getImages:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"person/"+b.id+"/images"+theMovieDb.common.generateQuery(b)},c,a)},getTaggedImages:function(c,a,b){theMovieDb.common.validateRequired(arguments,3,c,["id"]);theMovieDb.common.validateCallbacks([success,b]);theMovieDb.common.client({url:"person/"+c.id+"/tagged_images"+theMovieDb.common.generateQuery(c)},success,b)},getChanges:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"person/"+b.id+"/changes"+theMovieDb.common.generateQuery(b)},c,a)},getPopular:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,"","",true);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"person/popular"+theMovieDb.common.generateQuery(b)},c,a)},getLatest:function(b,a){theMovieDb.common.validateRequired(arguments,2);theMovieDb.common.validateCallbacks([b,a]);theMovieDb.common.client({url:"person/latest"+theMovieDb.common.generateQuery()},b,a)}};theMovieDb.reviews={getById:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"review/"+b.id+theMovieDb.common.generateQuery(b)},c,a)}};theMovieDb.search={getMovie:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["query"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"search/movie"+theMovieDb.common.generateQuery(b)},c,a)},getCollection:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["query"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"search/collection"+theMovieDb.common.generateQuery(b)},c,a)},getTv:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["query"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"search/tv"+theMovieDb.common.generateQuery(b)},c,a)},getPerson:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["query"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"search/person"+theMovieDb.common.generateQuery(b)},c,a)},getList:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["query"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"search/list"+theMovieDb.common.generateQuery(b)},c,a)},getCompany:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["query"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"search/company"+theMovieDb.common.generateQuery(b)},c,a)},getKeyword:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["query"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"search/keyword"+theMovieDb.common.generateQuery(b)},c,a)}};theMovieDb.timezones={getList:function(b,a){theMovieDb.common.validateRequired(arguments,2);theMovieDb.common.validateCallbacks([b,a]);theMovieDb.common.client({url:"timezones/list"+theMovieDb.common.generateQuery()},b,a)}};theMovieDb.tv={getById:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"tv/"+b.id+theMovieDb.common.generateQuery(b)},c,a)},getCredits:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"tv/"+b.id+"/credits"+theMovieDb.common.generateQuery(b)},c,a)},getExternalIds:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"tv/"+b.id+"/external_ids"+theMovieDb.common.generateQuery(b)},c,a)},getImages:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"tv/"+b.id+"/images"+theMovieDb.common.generateQuery(b)},c,a)},getTranslations:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"tv/"+b.id+"/translations"+theMovieDb.common.generateQuery(b)},c,a)},getOnTheAir:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,"","",true);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"tv/on_the_air"+theMovieDb.common.generateQuery(b)},c,a)},getAiringToday:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,"","",true);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"tv/airing_today"+theMovieDb.common.generateQuery(b)},c,a)},getTopRated:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,"","",true);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"tv/top_rated"+theMovieDb.common.generateQuery(b)},c,a)},getPopular:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,"","",true);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"tv/popular"+theMovieDb.common.generateQuery(b)},c,a)}};theMovieDb.tvSeasons={getById:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["season_number","id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"tv/"+b.id+"/season/"+b.season_number+theMovieDb.common.generateQuery(b)},c,a)},getCredits:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["season_number","id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"tv/"+b.id+"/season/"+b.season_number+"/credits"+theMovieDb.common.generateQuery(b)},c,a)},getExternalIds:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["season_number","id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"tv/"+b.id+"/season/"+b.season_number+"/external_ids"+theMovieDb.common.generateQuery(b)},c,a)},getImages:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["season_number","id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"tv/"+b.id+"/season/"+b.season_number+"/images"+theMovieDb.common.generateQuery(b)},c,a)}};theMovieDb.tvEpisodes={getById:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["episode_number","season_number","id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"tv/"+b.id+"/season/"+b.season_number+"/episode/"+b.episode_number+theMovieDb.common.generateQuery(b)},c,a)},getCredits:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["episode_number","season_number","id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"tv/"+b.id+"/season/"+b.season_number+"/episode/"+b.episode_number+"/credits"+theMovieDb.common.generateQuery(b)},c,a)},getExternalIds:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["episode_number","season_number","id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"tv/"+b.id+"/season/"+b.season_number+"/episode/"+b.episode_number+"/external_ids"+theMovieDb.common.generateQuery(b)},c,a)},getImages:function(b,c,a){theMovieDb.common.validateRequired(arguments,3,b,["episode_number","season_number","id"]);theMovieDb.common.validateCallbacks([c,a]);theMovieDb.common.client({url:"tv/"+b.id+"/season/"+b.season_number+"/episode/"+b.episode_number+"/images"+theMovieDb.common.generateQuery(b)},c,a)}};

Parse.initialize("UFj25hlpaoyfnC2w9sPUcxynB1tfxEtoP2uexm9W", "GTQKWnICXcKzzgrVt8bS0tUpWrTXeqiEQjA9hHLj");

load();
var movies = [];

function load() {
}

function addRow(movieTitle) {
	var movieList = document.getElementById("movieList");
	var entry = document.createElement('li');
	entry.className = "li";
	entry.appendChild(document.createTextNode(movieTitle));

	var button = document.createElement("input");
	button.id = movieList.childNodes.length+1;
	button.type = "button";
	button.value = "Delete";
	button.onclick = deleteRow;
	entry.appendChild(button);

	movieList.appendChild(entry);
}

function deleteRow() {
	for (var i=0; i<movies.length; i++) {
		var elementTitle = this.parentNode.firstChild.nodeValue;
		if (elementTitle === movies[i].get("title")) {
			movies.splice(i, 1);
		}
	}
	this.parentNode.parentNode.removeChild(this.parentNode);

	var printStr = "Movies:";
	for (var i=0; i<movies.length; i++) {
		printStr += " movie[" + i + "] " + movies[i].get("title");
	}
	console.log(printStr);
}

function successCB(data) {
	var photoList = document.getElementById("photos");

	var obj = JSON.parse(data);
	for (var i in obj.backdrops) {
		var imageUrl = theMovieDb.common.images_uri + "w500" + obj.backdrops[i].file_path;
		var oImg = document.createElement("img");
		var checkbox = document.createElement('input');
		checkbox.checked = true;
		checkbox.type = "checkbox";
		checkbox.id = obj.backdrops[i].file_path;
		oImg.onclick = makeOnclick(checkbox)
		var text = document.createTextNode(imageUrl);
		oImg.setAttribute('src', imageUrl);
		var li = document.createElement("li");
		li.appendChild(oImg);
		li.appendChild(text);
		li.appendChild(checkbox);
		photoList.appendChild(li);
	}
};

function errorCB(data) {
	alert("ERROR");
	console.log("Error callback: " + data);
};

function populateMovies() {
	var startId = 100;
	for (var i=startId; i<startId+20; i++) {
		theMovieDb.movies.getById({"id":i }, function(data) {
			var movieObj = JSON.parse(data);
			var Movie = Parse.Object.extend("Movie");
			var query = new Parse.Query(Movie);
			query.equalTo("mdId", movieObj.id);
			query.find({
				success: function(results) {
					if (results.length > 0) {
						console.log(movieObj.original_title + " with id " + movieObj.id + " already in parse DB");
					} else {
						console.log("Will add " + movieObj.original_title + " mdId " + movieObj.id);
					}
				},
				error: function(error) {
					alert("Error: " + error.code + " " + error.message);
				}
			});
		}, 
		function(data) {
			console.log("no movie with that id");
		});
	}
}

function searchMovie() {
	var title = document.getElementById("searchBox").value;

	var Movie = Parse.Object.extend("Movie");
	var query = new Parse.Query(Movie);
	query.startsWith("title", title);
	query.find({
		success: function(results) {
			if (results.length > 0) {
				addRow(results[0].get("title"));
				movies.push(results[0]);

				var printStr = "Movies:";
				for (var i=0; i<movies.length; i++) {
					printStr += " movie[" + i + "] " + movies[i].get("title");
				}
				console.log(printStr);
			} else {
				alert("Movie not found in database, add the movie first!");
				console.log("Will add " + movieObj.original_title + " mdId " + movieObj.id);
			}
		},
		error: function(error) {
			alert("Error: " + error.code + " " + error.message);
		}
	});
}

function submitLevel() {
	if (validate()) {
		var Level = Parse.Object.extend("Level");
		var level = new Level();

		var levelName = document.getElementById("levelName").value;
		var levelId = document.getElementById("levelId").value;
		var levelIdNum = parseInt(levelId, 10);

		var printStr = "Movies:";
		for (var i=0; i<movies.length; i++) {
			printStr += " movie[" + i + "] " + movies[i].get("title");
		}
		console.log(printStr);

		level.set("levelNumber", levelIdNum);
		level.set("name", levelName);
		var relation = level.relation("movies");
		for (var i=0; i<movies.length; i++) {
			relation.add(movies[i]);
		}

		level.save(null, {
			success: function(gameScore) {
				// Execute any logic that should take place after the object is saved.
				alert('New object created with objectId: ' + gameScore.id);
				document.getElementById("levelName").value = '';
				document.getElementById("levelId").value = '';
				document.getElementById("searchBox").value = '';
				document.getElementById("movieList").innerHTML = "";
				movies = [];
			},
			error: function(gameScore, error) {
				// Execute any logic that should take place if the save fails.
				// error is a Parse.Error with an error code and message.
				alert('Failed to create new object, with error code: ' + error.message);
			}
		});
	}
}

function validate() {
	var levelName = document.getElementById("levelName").value;
	var levelId = document.getElementById("levelId").value;
	var numMovies = document.getElementById("movieList").childNodes.length;

	if (levelName === '' || levelId === '' || numMovies == 0) {
		alert("Fill all the fields and add at least one movie!");
		return false;
	} else {
		return true;
	}
}

