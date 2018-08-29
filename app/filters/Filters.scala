package filters

import javax.inject.Inject

import play.api.http.DefaultHttpFilters

class Filters @Inject()(login: LoginFilter) extends DefaultHttpFilters(login)