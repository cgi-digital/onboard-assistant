package onboard.assistant;

public final class IncomingMessage {

    public String token;
    public String teamm_id;
    public String api_app_id;
    public Event event;
    public String type;
    public String event_id;
    public int event_time;
    public String[] authed_users;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTeamm_id() {
        return teamm_id;
    }

    public void setTeamm_id(String teamm_id) {
        this.teamm_id = teamm_id;
    }

    public String getApi_app_id() {
        return api_app_id;
    }

    public void setApi_app_id(String api_app_id) {
        this.api_app_id = api_app_id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public int getEvent_time() {
        return event_time;
    }

    public void setEvent_time(int event_time) {
        this.event_time = event_time;
    }

    public String[] getAuthed_users() {
        return authed_users;
    }

    public void setAuthed_users(String[] authed_users) {
        this.authed_users = authed_users;
    }
}
