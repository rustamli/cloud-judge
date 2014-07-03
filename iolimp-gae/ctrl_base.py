
import webapp2
from ctrl_accounts import *
from model.base import *

from webapp2_extras import sessions

class Page(webapp2.RequestHandler):
    def dispatch(self):
        self.session_store = sessions.get_store(request=self.request)

        try:
            webapp2.RequestHandler.dispatch(self)
        finally:
            self.session_store.save_sessions(self.response)

    @webapp2.cached_property
    def session(self):
        return self.session_store.get_session()

    def get_template_name(self):
        return "not-found"

    def get_template_values(self, lang, path_params, current_account):
        return {
            "lang": lang,
            "account": cj_util.get_current_account_dict(current_account)
        }

    def render_page(self, path_params = {}):
        self.response.headers["Content-Type"] = "text/html"
        lang = cj_util.resolve_lang(self)

        account_key = self.session.get("account")
        account = None
        if account_key:
            account = Account.get(account_key)


        template_values = self.get_template_values(lang, path_params, account)

        cj_util.output(self, self.get_template_name(), lang, template_values)

    def get(self):
        self.render_page()


class MainPage(Page):
    def get_template_name(self):
        return "index"