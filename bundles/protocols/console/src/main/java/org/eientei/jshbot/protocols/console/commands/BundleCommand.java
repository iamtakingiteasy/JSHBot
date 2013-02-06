package org.eientei.jshbot.protocols.console.commands;

import jline.console.completer.Completer;
import org.eientei.jshbot.protocols.console.api.ConsoleCommand;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-05
 * Time: 12:04
 */
public class BundleCommand implements ConsoleCommand {
    @Override
    public String[][] getMountPoints() {
        return new String[0][0];
    }

    @Override
    public String getDesc() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<Completer> getCompleters() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void execute(List<String> arguments) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /*
    private GenericSingularServiceListener<Dispatcher> dispatcherService;
    private BundleContext bundleContext;
    private SortedSet<String> subCommands = new TreeSet<String>() {{
        add("update");
        add("stop");
        add("start");
        add("uninstall");
        add("restart");
        add("list");
        add("install");
    }};

    public BundleCommand(GenericSingularServiceListener<Dispatcher> dispatcherService, BundleContext bundleContext) {
        this.dispatcherService = dispatcherService;
        this.bundleContext = bundleContext;
    }

    @Override
    public String commandName() {
        return "bundle";
    }

    @Override
    public String commandDesc() {
        return "Bundle operations";
    }

    @Override
    public Collection<Completer> getCompleters() {
        List<Completer> completers = new ArrayList<Completer>();
        completers.add(new Completer() {
            @Override
            public int complete(String buffer, int cursor, List<CharSequence> candidates) {
                String cmd = buffer.replaceFirst("bundle","").trim();

                if (buffer == null) {
                    candidates.addAll(subCommands);
                } else {
                    for (String match : subCommands.tailSet(cmd)) {
                        if (!match.startsWith(cmd)) {
                            break;
                        }
                        candidates.add(match);
                    }
                }

                if (candidates.size() == 1) {
                    candidates.set(0, candidates.get(0) + " ");
                }

                return (candidates.isEmpty()) ? -1 : "bundle ".length();
            }
        });

        completers.add(new Completer() {
            @Override
            public int complete(String buffer, int cursor, List<CharSequence> candidates) {
                if (buffer.matches("^bundle[ ]*install[ ]*file:.*$")) {
                    String path = buffer.replaceFirst(".*install[ ]*file:","");
                    int diff = buffer.length() - path.length();
                    return new FileNameCompleter().complete(path,cursor-diff,candidates) + diff;
                }
                return -1;
            }
        });

        return completers;
    }

    @Override
    public boolean matches(String[] arguments) {
        return true;
    }

    @Override
    public void execute(String[] arguments) {
        String operation = "list";

        if (arguments.length > 1) {
            operation = arguments[1];
        }


        if (operation.equals("list")) {
            listBundles();
        } else if (operation.equals("install")) {
            if (arguments.length > 2) {
                installBundle(arguments[2]);
            }
        } else {
            final int bundleId;
            final String op;
            if (arguments.length > 2 ) {
                if (arguments[1].matches("^[0-9]+$")) {
                    op = arguments[2];
                    bundleId = Integer.parseInt(arguments[1]);
                } else {
                    op = operation;
                    bundleId = Integer.parseInt(arguments[2]);
                }
            } else {
                op = "";
                bundleId = -1;
            }

            if (bundleId == bundleContext.getBundle().getBundleId()) {
                new Thread() {
                    @Override
                    public void run() {
                        if (op.equals("stop")) {
                            stopBundle(bundleId);
                        } else if (op.equals("start")) {
                            startBundle(bundleId);
                        } else if (op.equals("uninstall")) {
                            uninstallBundle(bundleId);
                        } else if (op.equals("update")) {
                            updateBundle(bundleId);
                        } else if (op.equals("restart")) {
                            restartBundle(bundleId);
                        }
                    }
                }.start();
            } else {
                if (op.equals("stop")) {
                    stopBundle(bundleId);
                } else if (op.equals("start")) {
                    startBundle(bundleId);
                } else if (op.equals("uninstall")) {
                    uninstallBundle(bundleId);
                } else if (op.equals("update")) {
                    updateBundle(bundleId);
                } else if (op.equals("restart")) {
                    restartBundle(bundleId);
                }
            }
        }
    }

    private void restartBundle(int i) {
        Bundle b = bundleContext.getBundle(i);
        if (b != null) {
            try {
                b.stop();
                b.start();
                Message message = new Message(URI.create("console://stdin"), URI.create("console://stdout"), "Bundle " + i + " restarted.");
                dispatcherService.getOrWaitForServiceInstance().dispatch(message);
            } catch (BundleException e) {
                e.printStackTrace();
            }
        }
    }

    private void installBundle(String argument) {
        try {
            Bundle b = bundleContext.installBundle(argument);
            Message message = new Message(URI.create("console://stdin"), URI.create("console://stdout"), "Bundle " + b.getSymbolicName() + " installed at id " + b.getBundleId() + ".");
            dispatcherService.getOrWaitForServiceInstance().dispatch(message);
        } catch (BundleException e) {
            e.printStackTrace();
        }
    }

    private void updateBundle(int i) {
        Bundle b = bundleContext.getBundle(i);
        if (b != null) {
            try {
                b.update();
                Message message = new Message(URI.create("console://stdin"), URI.create("console://stdout"), "Bundle " + i + " updated.");
                dispatcherService.getOrWaitForServiceInstance().dispatch(message);
            } catch (BundleException e) {
                e.printStackTrace();
            }
        }
    }

    private void uninstallBundle(int i) {
        Bundle b = bundleContext.getBundle(i);
        if (b != null) {
            try {
                b.uninstall();
                Message message = new Message(URI.create("console://stdin"), URI.create("console://stdout"), "Bundle " + i + " uninstalled.");
                dispatcherService.getOrWaitForServiceInstance().dispatch(message);
            } catch (BundleException e) {
                e.printStackTrace();
            }
        }
    }

    private void startBundle(int i) {
        Bundle b = bundleContext.getBundle(i);
        if (b != null) {
            try {
                b.start();
                Message message = new Message(URI.create("console://stdin"), URI.create("console://stdout"), "Bundle " + i + " started.");
                dispatcherService.getOrWaitForServiceInstance().dispatch(message);
            } catch (BundleException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopBundle(int i) {
        final Bundle b = bundleContext.getBundle(i);
        if (b != null) {
            try {
                b.stop();
                Message message = new Message(URI.create("console://stdin"), URI.create("console://stdout"), "Bundle " + i + " stopped.");
                dispatcherService.getOrWaitForServiceInstance().dispatch(message);
            } catch (BundleException e) {
                e.printStackTrace();
            }
        }
    }

    private void listBundles() {
        StringBuilder sb = new StringBuilder();
        Bundle[] bundles = bundleContext.getBundles();
        boolean first = true;
        if (bundles != null) {
            for (Bundle b : bundles) {
                if (!first) {
                    sb.append("\n");
                } else {
                    first = false;
                }
                switch (b.getState()) {
                    case Bundle.ACTIVE:
                        sb.append("     ACTIVE");
                        break;
                    case Bundle.STARTING:
                        sb.append("   STARTING");
                        break;
                    case Bundle.STOPPING:
                        sb.append("   STOPPING");
                        break;
                    case Bundle.INSTALLED:
                        sb.append("  INSTALLED");
                        break;
                    case Bundle.UNINSTALLED:
                        sb.append("UNINSTALLED");
                        break;
                    case Bundle.RESOLVED:
                        sb.append("   RESOLVED");
                        break;
                    default:
                        sb.append("    UNKNOWN");
                        break;
                }
                sb.append("    ");
                sb.append(String.format("%6d", b.getBundleId()));
                sb.append("    ");
                sb.append(b.getSymbolicName());
            }
        }
        Message message = new Message(URI.create("console://stdin"), URI.create("console://stdout"), sb.toString());
        dispatcherService.getOrWaitForServiceInstance().dispatch(message);
    }
    */
}
