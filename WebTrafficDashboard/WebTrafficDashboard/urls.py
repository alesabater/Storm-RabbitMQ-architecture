from django.conf.urls import patterns, include, url
from django.contrib.staticfiles.urls import staticfiles_urlpatterns

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

urlpatterns = patterns('',
    
    url(r'^$', 'Dashboard.views.index', name='index'),
    url(r'^home/$', 'Dashboard.views.prueba', name='index'),
    # Examples:
    # url(r'^$', 'WebTrafficDashboard.views.home', name='home'),
    # url(r'^WebTrafficDashboard/', include('WebTrafficDashboard.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    # url(r'^admin/', include(admin.site.urls)),
)
urlpatterns += staticfiles_urlpatterns()