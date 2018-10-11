# -*- coding: utf-8 -*-
"""
@author: sorr

This module provides a convenience function for making RESTful requests with
exponential backoff (that is, if a request results in a transient error, the
function makes the request again several times, with increasing delays between
each attempt).

"""

from sys import stdout, stderr
from time import sleep
from random import randint

from requests import Session, Request, ConnectionError, Timeout, \
                     ConnectTimeout
import urllib3

from logger import package_logger


# Set up logging.
logger = package_logger.getChild(__name__)

# Create a session object.
session = Session()

# We don't need a warning every time we connect with "verify" set to False.
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)


def get_response(URL, method = "get", verify = True, cert = None, params = None,
                 data = None, json = None, headers = None, delay = 1,
                 max_attempts = 8):
    """
    Make a RESTful API request with exponential backoff, and return
    the response.  This is a wrapper for the :mod:`requests` package.

    :param URL:  the URL of the service to be called
    :param method:  the HTML method for making the request
    :param verify:  a flag indicating whether or not to verify the server's
        certificate.  This flag is only meaningful for SSL
        connections, and is necessary in development environments with
        servers using self-signed certificates.
    :param cert:  the path(s) to a client-side certificate and/or key.  If
        separate certificate and key are to be passed, this should be a
        tuple of the two paths.
    :param params:  the parameters of the request
    :param data:  the data payload of the request.  A payload submitted
        using this object will be transferred to the API server as a
        series of values joined by "&".
    :param json:  a data payload in JSON form, or a PyThon object to be
        JSON-encoded
    :param headers:  the headers of the request
    :param delay:  the delay for the first retry.  Later retries
        increase the delay by factors of 2.
    :param max_attempts:  the maximum number of times to attempt
        the request

    :returns:  whatever response is returned for the request, as a
        :class:`requests.Response` object

    """

    # Set the verify flag for the sessions.
    session.verify = verify

    # If necessary, add a client-side certificate and/or key to the
    # session.
    session.cert = cert

    # Create a dict of keyword arguments (this allows optional arguments
    # to be omitted entirely).  We encode any "data" block in UTF-8
    # because requests can choke when attempting to send Unicode.
    req_kwargs = {}
    status = None
    for kwarg_key in ("params", "data", "json", "headers"):
        kwarg_value = locals()[kwarg_key]
        if kwarg_value is not None:
            if kwarg_key == "data":
                req_kwargs[kwarg_key] = kwarg_value.encode("utf-8")
            else:
                req_kwargs[kwarg_key] = kwarg_value

    # Create request objects.  Doing it this way, rather than with
    # "requests.request" (or ".get" or ".post"), lets us pass the request
    # object as an argument when raising a ConnectTimeout.  Using a
    # persistent session object (which is required for this approach)
    # should also have peformance advantages.
    request = Request(method = method, url = URL, **req_kwargs)
    prepped_req = request.prepare()

#    # Diagnostic code for printing the raw request.
#    stdout.write('\n{}\n{}\n\n{}\n'.format(
#                 prepped_req.method + ' ' + prepped_req.url,
#                 '\n'.join('{}:  {}'.format(k, v)
#                 for k, v in prepped_req.headers.items()),
#                 'Request body:  ' + str(prepped_req.body)))
#    stdout.flush()

    # Call the API, using exponential backoff as necessary.

    for attempt in xrange(max_attempts):

        try:

            response = session.send(prepped_req)
            status = response.status_code

            if 200 <= status < 300:
                return response

            # If this is a transient error, we want to retry.
            elif status in [502, 503, 504]:
                try:
                    response.raise_for_status()
                except Exception as err:
                    logger.warn('Server at ' + URL + ' returned status code "' +
                                str(status) + '" with the following error ' +
                                'message:\n' + str(err) + '\nRetrying....')
                    stderr.flush()

            # In some cases, retrying won't do us any good.
            else:
                logger.warn("Received the following error message from server " +
                            "at " + URL + ":\n" + response.text)
                stderr.flush()
                response.raise_for_status()

        except (ConnectionError, Timeout) as err:
            logger.warn("Attempt to connect to " + URL + " failed with the " +
                        "following error message:\n" + str(err) +
                        "\nRetrying....")
            stderr.flush()

        fuzz = randint(1,1000) / 1000
        sleep(delay + fuzz)
        delay = 2 * delay

    # If no valid response is received, raise an error, with the status
    # code (if any) of the last response as the error message.
    if status:
        status_code = "status code " + str(status)
    else:
        status_code = "no response from server"
    err_msg = "Unable to complete request to " + URL + ":  " + status_code + "."
    raise ConnectTimeout(err_msg, request = request)

