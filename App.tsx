import React, { useState, useEffect, useId } from 'react';
import {
  Shield,
  Download,
  Smartphone,
  CheckCircle2,
  AlertTriangle,
  MapPin,
  Users,
  Navigation,
  Activity,
  BellRing,
  Radio,
  QrCode,
  Sparkles,
  Info,
  PhoneCall,
  Volume2,
  Lock
} from 'lucide-react';

interface Contact {
  id: string;
  name: string;
  relation: string;
  phone: string;
}

export default function App() {
  const [activeTab, setActiveTab] = useState<'journey' | 'monitoring' | 'sos' | 'guardians'>('journey');
  const [downloadSuccess, setDownloadSuccess] = useState(false);
  const [qrOpen, setQrOpen] = useState(false);

  // Form states
  const [destination, setDestination] = useState('Central Metro Station');
  const [transportMode, setTransportMode] = useState<'CAB' | 'WALKING' | 'TRANSIT'>('CAB');
  const [etaMinutes, setEtaMinutes] = useState(25);
  const [isJourneyActive, setIsJourneyActive] = useState(false);

  // Live monitor simulation state
  const [elapsedSeconds, setElapsedSeconds] = useState(0);
  const [deviationDetected, setDeviationDetected] = useState(false);
  const [safetyCountdown, setSafetyCountdown] = useState<number | null>(null);
  const [isSosTriggered, setIsSosTriggered] = useState(false);

  // Contacts
  const [contacts, setContacts] = useState<Contact[]>([
    { id: '1', name: 'Mom (Radha Sharma)', relation: 'Primary Guardian', phone: '+91 98765 43210' },
    { id: '2', name: 'Aarav Sharma', relation: 'Brother', phone: '+91 91234 56789' },
    { id: '3', name: 'Pooja Verma', relation: 'Roommate', phone: '+91 94567 89012' },
  ]);

  const [newContactName, setNewContactName] = useState('');
  const [newContactPhone, setNewContactPhone] = useState('');
  const [newContactRel, setNewContactRel] = useState('');

  const destInputId = useId();
  const etaSelectId = useId();
  const nameInputId = useId();
  const relInputId = useId();
  const phoneInputId = useId();

  // Monitor timer
  useEffect(() => {
    let interval: NodeJS.Timeout | null = null;
    if (isJourneyActive) {
      interval = setInterval(() => {
        setElapsedSeconds((prev) => prev + 1);
      }, 1000);
    }
    return () => {
      if (interval) clearInterval(interval);
    };
  }, [isJourneyActive]);

  // Countdown timer for anomaly
  useEffect(() => {
    let timer: NodeJS.Timeout | null = null;
    if (safetyCountdown !== null && safetyCountdown > 0) {
      timer = setInterval(() => {
        setSafetyCountdown((prev) => (prev !== null && prev > 0 ? prev - 1 : 0));
      }, 1000);
    } else if (safetyCountdown === 0) {
      setIsSosTriggered(true);
      setActiveTab('sos');
      setSafetyCountdown(null);
    }
    return () => {
      if (timer) clearInterval(timer);
    };
  }, [safetyCountdown]);

  const handleStartJourney = () => {
    setIsJourneyActive(true);
    setElapsedSeconds(0);
    setDeviationDetected(false);
    setSafetyCountdown(null);
    setIsSosTriggered(false);
    setActiveTab('monitoring');
  };

  const handleStopJourney = () => {
    setIsJourneyActive(false);
    setDeviationDetected(false);
    setSafetyCountdown(null);
    setActiveTab('journey');
  };

  const triggerSimulatedAnomaly = () => {
    setDeviationDetected(true);
    setSafetyCountdown(30);
  };

  const handleResolveAnomaly = () => {
    setDeviationDetected(false);
    setSafetyCountdown(null);
  };

  const handleTriggerSOS = () => {
    setIsSosTriggered(true);
    setActiveTab('sos');
  };

  const handleCancelSOS = () => {
    setIsSosTriggered(false);
    setActiveTab('monitoring');
  };

  const handleAddContact = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newContactName.trim() || !newContactPhone.trim()) return;
    setContacts((prev) => [
      ...prev,
      {
        id: Date.now().toString(),
        name: newContactName.trim(),
        phone: newContactPhone.trim(),
        relation: newContactRel.trim() || 'Guardian',
      },
    ]);
    setNewContactName('');
    setNewContactPhone('');
    setNewContactRel('');
  };

  const handleDeleteContact = (id: string) => {
    setContacts((prev) => prev.filter((c) => c.id !== id));
  };

  const formatTime = (secs: number) => {
    const mins = Math.floor(secs / 60);
    const s = secs % 60;
    return `${mins.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  };

  const handleDownloadClick = () => {
    setDownloadSuccess(true);
    setTimeout(() => setDownloadSuccess(false), 4000);
  };

  const currentUrl = typeof window !== 'undefined' ? window.location.href : '';
  const qrCodeUrl = `https://api.qrserver.com/v1/create-qr-code/?size=240x240&data=${encodeURIComponent(
    currentUrl.replace(/\/$/, '') + '/abhaya-kavach.apk'
  )}`;

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col justify-between">
      {/* Top App Header */}
      <header className="border-b border-slate-800 bg-slate-900/70 backdrop-blur-md sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-rose-600 to-amber-500 flex items-center justify-center shadow-lg shadow-rose-950/50">
              <Shield className="w-5 h-5 text-white" />
            </div>
            <div>
              <div className="flex items-center space-x-2">
                <h1 className="font-bold text-lg text-white tracking-tight">Abhaya Kavach</h1>
                <span className="text-[10px] uppercase font-bold tracking-wider px-2 py-0.5 rounded-full bg-rose-500/10 text-rose-400 border border-rose-500/20">
                  Android APK v1.0
                </span>
              </div>
              <p className="text-xs text-slate-400">Proactive Women's Safety & Monitored Journey Shield</p>
            </div>
          </div>

          <div className="flex items-center space-x-3">
            <button
              id="qr-btn"
              onClick={() => setQrOpen(!qrOpen)}
              className="px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-xs font-semibold text-slate-300 transition-colors flex items-center space-x-1.5 border border-slate-700 cursor-pointer"
            >
              <QrCode className="w-3.5 h-3.5 text-rose-400" />
              <span>Scan QR</span>
            </button>
            <a
              id="header-download-apk"
              href="/abhaya-kavach.apk"
              download="abhaya-kavach.apk"
              onClick={handleDownloadClick}
              className="px-4 py-2 rounded-xl bg-gradient-to-r from-rose-600 to-rose-500 hover:from-rose-500 hover:to-rose-400 text-white text-xs sm:text-sm font-bold shadow-md shadow-rose-600/30 flex items-center space-x-2 transition-all transform active:scale-95"
            >
              <Download className="w-4 h-4" />
              <span>Download APK</span>
            </a>
          </div>
        </div>
      </header>

      {/* Main Container */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full flex-1">
        {/* Hero Section */}
        <div className="mb-8 rounded-2xl bg-gradient-to-br from-slate-900 via-slate-900/90 to-rose-950/40 border border-slate-800 p-6 sm:p-8 shadow-2xl relative overflow-hidden">
          <div className="absolute -top-24 -right-24 w-96 h-96 bg-rose-500/10 rounded-full blur-3xl pointer-events-none" />
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-center relative z-10">
            <div className="lg:col-span-8 space-y-4">
              <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-rose-500/10 text-rose-300 text-xs font-semibold border border-rose-500/20">
                <Sparkles className="w-3.5 h-3.5 text-rose-400" />
                <span>Compatible with Android 7.0+ (API 24 to 35)</span>
              </div>
              <h2 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
                <span className="text-transparent bg-clip-text bg-gradient-to-r from-rose-400 to-amber-300">Abhaya Kavach</span> — Smart Safety Companion
              </h2>
              <p className="text-slate-300 text-sm sm:text-base leading-relaxed max-w-2xl">
                Real-time route deviation checks, automated countdown SOS triggers, emergency audio recording, and direct guardian location broadcasts.
              </p>

              {/* Action Buttons */}
              <div className="flex flex-wrap items-center gap-3 pt-2">
                <a
                  id="hero-download-apk"
                  href="/abhaya-kavach.apk"
                  download="abhaya-kavach.apk"
                  onClick={handleDownloadClick}
                  className="px-6 py-3.5 rounded-xl bg-rose-600 hover:bg-rose-500 text-white font-bold text-sm shadow-xl shadow-rose-600/30 flex items-center space-x-2.5 transition-all transform active:scale-95"
                >
                  <Download className="w-5 h-5" />
                  <span>Download abhaya-kavach.apk</span>
                </a>
                <button
                  id="toggle-qr-banner"
                  onClick={() => setQrOpen(!qrOpen)}
                  className="px-4 py-3.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-rose-300 font-semibold text-sm border border-slate-700 flex items-center space-x-2 transition-colors cursor-pointer"
                >
                  <QrCode className="w-4 h-4" />
                  <span>{qrOpen ? 'Hide Phone QR' : 'Show Phone QR'}</span>
                </button>
              </div>

              {downloadSuccess && (
                <div className="flex items-center space-x-2 text-emerald-400 bg-emerald-950/50 border border-emerald-500/30 px-3.5 py-2 rounded-lg text-xs">
                  <CheckCircle2 className="w-4 h-4 text-emerald-400" />
                  <span>Download initiated! Check your browser's download manager.</span>
                </div>
              )}
            </div>

            {/* Installation Steps Card */}
            <div className="lg:col-span-4 bg-slate-950/80 rounded-xl p-5 border border-slate-800 space-y-3">
              <h3 className="text-xs font-bold uppercase tracking-wider text-slate-400 flex items-center space-x-1.5">
                <Smartphone className="w-4 h-4 text-rose-400" />
                <span>Quick Phone Setup</span>
              </h3>
              <ol className="space-y-2.5 text-xs text-slate-300">
                <li className="flex items-start space-x-2">
                  <span className="w-5 h-5 rounded-full bg-rose-500/20 text-rose-400 flex items-center justify-center font-bold text-[10px] shrink-0 mt-0.5">
                    1
                  </span>
                  <span>Download the APK on your Android device.</span>
                </li>
                <li className="flex items-start space-x-2">
                  <span className="w-5 h-5 rounded-full bg-rose-500/20 text-rose-400 flex items-center justify-center font-bold text-[10px] shrink-0 mt-0.5">
                    2
                  </span>
                  <span>Enable <em>"Install unknown apps"</em> when prompted.</span>
                </li>
                <li className="flex items-start space-x-2">
                  <span className="w-5 h-5 rounded-full bg-rose-500/20 text-rose-400 flex items-center justify-center font-bold text-[10px] shrink-0 mt-0.5">
                    3
                  </span>
                  <span>Launch Abhaya Kavach and grant location permissions.</span>
                </li>
              </ol>
            </div>
          </div>

          {/* QR Code Expansion */}
          {qrOpen && (
            <div className="mt-6 pt-6 border-t border-slate-800 flex flex-col sm:flex-row items-center gap-6 bg-slate-950/60 p-4 rounded-xl">
              <div className="bg-white p-3 rounded-xl shadow-md shrink-0">
                <img
                  src={qrCodeUrl}
                  alt="QR Code for APK Download"
                  className="w-36 h-36 object-contain"
                  onError={(e) => {
                    (e.target as HTMLElement).style.display = 'none';
                  }}
                />
              </div>
              <div className="space-y-2 text-center sm:text-left">
                <h4 className="font-bold text-white text-sm">Scan with Phone Camera</h4>
                <p className="text-xs text-slate-400 max-w-md leading-relaxed">
                  Scan the QR code with your phone camera to download the APK directly on your Android phone.
                </p>
                <div className="text-[11px] text-rose-400 flex items-center justify-center sm:justify-start space-x-1">
                  <Info className="w-3.5 h-3.5" />
                  <span>Direct link: {currentUrl.replace(/\/$/, '')}/abhaya-kavach.apk</span>
                </div>
              </div>
            </div>
          )}
        </div>

        {/* Live Simulator & Features */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
          {/* Left Column: Feature Highlights */}
          <div className="lg:col-span-5 space-y-6">
            <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-6 space-y-4">
              <div className="flex items-center space-x-2 text-rose-400">
                <Radio className="w-5 h-5 animate-pulse" />
                <h3 className="font-bold text-base text-white">Live Safety Simulation</h3>
              </div>
              <p className="text-xs text-slate-400 leading-relaxed">
                Test the proactive protection pipeline right in your browser. Start a journey, trigger an anomaly test, and watch the emergency escalation logic.
              </p>

              <div className="space-y-2.5 pt-2">
                <div className="text-xs font-semibold text-slate-300">Simulate Events:</div>
                <div className="grid grid-cols-2 gap-2">
                  <button
                    id="sim-anomaly-btn"
                    onClick={triggerSimulatedAnomaly}
                    disabled={!isJourneyActive || deviationDetected}
                    className={`px-3 py-2 rounded-xl text-xs font-semibold border flex items-center justify-center space-x-1.5 transition-colors cursor-pointer ${
                      isJourneyActive && !deviationDetected
                        ? 'bg-amber-500/10 border-amber-500/30 text-amber-300 hover:bg-amber-500/20'
                        : 'bg-slate-800/50 border-slate-800 text-slate-500 cursor-not-allowed'
                    }`}
                  >
                    <AlertTriangle className="w-3.5 h-3.5" />
                    <span>Trigger Anomaly</span>
                  </button>

                  <button
                    id="sim-sos-btn"
                    onClick={handleTriggerSOS}
                    className="px-3 py-2 rounded-xl text-xs font-semibold bg-rose-500/10 border border-rose-500/30 text-rose-300 hover:bg-rose-500/20 flex items-center justify-center space-x-1.5 transition-colors cursor-pointer"
                  >
                    <BellRing className="w-3.5 h-3.5" />
                    <span>Emergency SOS</span>
                  </button>
                </div>
              </div>
            </div>

            {/* Emergency Contacts Panel */}
            <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-6 space-y-4">
              <div className="flex items-center justify-between">
                <h3 className="font-bold text-base text-white flex items-center space-x-2">
                  <Users className="w-4 h-4 text-rose-400" />
                  <span>Configured Guardians</span>
                </h3>
                <span className="text-xs bg-slate-800 text-slate-300 px-2 py-0.5 rounded-full">
                  {contacts.length} Active
                </span>
              </div>
              <div className="space-y-2">
                {contacts.map((contact) => (
                  <div
                    key={contact.id}
                    className="flex items-center justify-between p-3 rounded-xl bg-slate-950/60 border border-slate-800/80 text-xs"
                  >
                    <div>
                      <div className="font-semibold text-slate-200">{contact.name}</div>
                      <div className="text-[11px] text-slate-400">{contact.phone} • {contact.relation}</div>
                    </div>
                    <span className="text-[10px] text-emerald-400 bg-emerald-950/50 px-2 py-0.5 rounded border border-emerald-500/20">
                      Auto-Alert
                    </span>
                  </div>
                ))}
              </div>
            </div>

            {/* Helplines */}
            <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-6 space-y-3">
              <h3 className="font-bold text-xs uppercase tracking-wider text-slate-400">
                Official Helplines (Emergency 1-Touch)
              </h3>
              <div className="grid grid-cols-2 gap-2 text-xs">
                <div className="p-2.5 rounded-xl bg-slate-950 border border-slate-800 flex items-center justify-between">
                  <span className="text-slate-300 font-medium">Police (ERSS)</span>
                  <span className="font-extrabold text-rose-400">112</span>
                </div>
                <div className="p-2.5 rounded-xl bg-slate-950 border border-slate-800 flex items-center justify-between">
                  <span className="text-slate-300 font-medium">Women Helpline</span>
                  <span className="font-extrabold text-amber-400">1091</span>
                </div>
              </div>
            </div>
          </div>

          {/* Right Column: Interactive Phone Mockup */}
          <div className="lg:col-span-7 flex justify-center">
            <div className="w-full max-w-[380px] bg-slate-900 rounded-[40px] p-3 shadow-2xl border-4 border-slate-800 relative">
              <div className="w-28 h-4 bg-slate-800 rounded-full mx-auto mb-2 flex items-center justify-center">
                <div className="w-3 h-3 rounded-full bg-slate-900 mr-2" />
                <div className="w-8 h-1 bg-slate-900 rounded-full" />
              </div>

              <div className="bg-slate-950 rounded-[32px] overflow-hidden border border-slate-800 flex flex-col min-h-[640px] relative">
                {/* Mobile Top Bar */}
                <div className="bg-slate-900/90 px-4 py-3 border-b border-slate-800 flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    <Shield className="w-4 h-4 text-rose-500" />
                    <span className="font-bold text-xs text-white">Abhaya Kavach</span>
                  </div>
                  <div className="flex items-center space-x-1.5">
                    <span className="w-2 h-2 rounded-full bg-emerald-400 animate-ping" />
                    <span className="text-[10px] text-emerald-400 font-semibold">GPS Active</span>
                  </div>
                </div>

                {/* Mobile Tab Navigation */}
                <div className="grid grid-cols-4 bg-slate-900/50 border-b border-slate-800 text-[10px] font-semibold text-slate-400">
                  <button
                    id="tab-journey"
                    onClick={() => setActiveTab('journey')}
                    className={`py-2 text-center transition-colors border-b-2 cursor-pointer ${
                      activeTab === 'journey'
                        ? 'border-rose-500 text-rose-400 bg-rose-500/5'
                        : 'border-transparent hover:text-slate-200'
                    }`}
                  >
                    Journey
                  </button>
                  <button
                    id="tab-monitor"
                    onClick={() => setActiveTab('monitoring')}
                    className={`py-2 text-center transition-colors border-b-2 cursor-pointer ${
                      activeTab === 'monitoring'
                        ? 'border-rose-500 text-rose-400 bg-rose-500/5'
                        : 'border-transparent hover:text-slate-200'
                    }`}
                  >
                    Monitor
                  </button>
                  <button
                    id="tab-guardians"
                    onClick={() => setActiveTab('guardians')}
                    className={`py-2 text-center transition-colors border-b-2 cursor-pointer ${
                      activeTab === 'guardians'
                        ? 'border-rose-500 text-rose-400 bg-rose-500/5'
                        : 'border-transparent hover:text-slate-200'
                    }`}
                  >
                    Guardians
                  </button>
                  <button
                    id="tab-sos"
                    onClick={() => setActiveTab('sos')}
                    className={`py-2 text-center transition-colors border-b-2 cursor-pointer ${
                      activeTab === 'sos'
                        ? 'border-rose-500 text-rose-400 bg-rose-500/5'
                        : 'border-transparent hover:text-slate-200'
                    }`}
                  >
                    SOS
                  </button>
                </div>

                {/* Mobile Content Area */}
                <div className="p-4 flex-1 overflow-y-auto space-y-4">
                  {/* TAB 1: JOURNEY SETUP */}
                  {activeTab === 'journey' && (
                    <div className="space-y-4">
                      <div className="bg-gradient-to-br from-rose-900/30 to-slate-900 p-4 rounded-2xl border border-rose-500/20">
                        <div className="text-xs font-bold text-rose-300 mb-1">Start Safe Journey</div>
                        <p className="text-[11px] text-slate-400 leading-relaxed">
                          Set destination and transport mode. Abhaya Kavach monitors route deviations in real-time.
                        </p>
                      </div>

                      <div className="space-y-3 text-xs">
                        <div>
                          <label htmlFor={destInputId} className="block text-slate-400 font-semibold mb-1">
                            Destination
                          </label>
                          <div className="relative">
                            <MapPin className="w-4 h-4 text-slate-500 absolute left-3 top-2.5" />
                            <input
                              id={destInputId}
                              type="text"
                              value={destination}
                              onChange={(e) => setDestination(e.target.value)}
                              className="w-full bg-slate-900 border border-slate-800 rounded-xl pl-9 pr-3 py-2 text-white focus:outline-none focus:border-rose-500"
                              placeholder="e.g. City Mall, University Campus"
                            />
                          </div>
                        </div>

                        <div>
                          <label className="block text-slate-400 font-semibold mb-1">Transport Mode</label>
                          <div className="grid grid-cols-3 gap-2">
                            {(['CAB', 'WALKING', 'TRANSIT'] as const).map((mode) => (
                              <button
                                key={mode}
                                type="button"
                                onClick={() => setTransportMode(mode)}
                                className={`py-2 rounded-xl text-center font-bold text-[11px] border transition-all cursor-pointer ${
                                  transportMode === mode
                                    ? 'bg-rose-600 border-rose-500 text-white'
                                    : 'bg-slate-900 border-slate-800 text-slate-400 hover:text-slate-200'
                                }`}
                              >
                                {mode}
                              </button>
                            ))}
                          </div>
                        </div>

                        <div>
                          <label htmlFor={etaSelectId} className="block text-slate-400 font-semibold mb-1">
                            Estimated Time (Minutes)
                          </label>
                          <select
                            id={etaSelectId}
                            value={etaMinutes}
                            onChange={(e) => setEtaMinutes(Number(e.target.value))}
                            className="w-full bg-slate-900 border border-slate-800 rounded-xl px-3 py-2 text-white focus:outline-none focus:border-rose-500"
                          >
                            <option value={10}>10 Minutes</option>
                            <option value={20}>20 Minutes</option>
                            <option value={25}>25 Minutes</option>
                            <option value={45}>45 Minutes</option>
                            <option value={60}>60 Minutes</option>
                          </select>
                        </div>

                        <button
                          id="mobile-start-journey"
                          onClick={handleStartJourney}
                          className="w-full py-3 rounded-xl bg-gradient-to-r from-rose-600 to-rose-500 hover:from-rose-500 hover:to-rose-400 text-white font-bold text-xs shadow-lg shadow-rose-600/30 flex items-center justify-center space-x-2 mt-4 transition-transform active:scale-95 cursor-pointer"
                        >
                          <Navigation className="w-4 h-4" />
                          <span>Start Monitored Journey</span>
                        </button>
                      </div>
                    </div>
                  )}

                  {/* TAB 2: LIVE MONITORING */}
                  {activeTab === 'monitoring' && (
                    <div className="space-y-4">
                      {isJourneyActive ? (
                        <>
                          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-4 space-y-3">
                            <div className="flex items-center justify-between">
                              <span className="text-[11px] font-bold text-emerald-400 flex items-center space-x-1">
                                <Activity className="w-3.5 h-3.5" />
                                <span>Journey in Progress</span>
                              </span>
                              <span className="text-xs font-mono font-bold text-slate-300">
                                {formatTime(elapsedSeconds)} / {etaMinutes}:00
                              </span>
                            </div>

                            <div className="text-xs space-y-1">
                              <div className="text-slate-400">Heading to:</div>
                              <div className="font-bold text-white text-sm">{destination}</div>
                              <div className="text-[11px] text-slate-500">Mode: {transportMode}</div>
                            </div>

                            <div className="space-y-1">
                              <div className="h-2 w-full bg-slate-800 rounded-full overflow-hidden">
                                <div
                                  className="h-full bg-gradient-to-r from-emerald-500 to-rose-500 transition-all duration-1000"
                                  style={{
                                    width: `${Math.min(100, (elapsedSeconds / (etaMinutes * 60)) * 100)}%`,
                                  }}
                                />
                              </div>
                            </div>
                          </div>

                          {deviationDetected && (
                            <div className="p-4 rounded-2xl bg-amber-950/70 border-2 border-amber-500/50 space-y-3 animate-pulse">
                              <div className="flex items-center space-x-2 text-amber-300">
                                <AlertTriangle className="w-5 h-5 text-amber-400" />
                                <span className="font-bold text-xs">Route Anomaly Detected!</span>
                              </div>
                              <p className="text-[11px] text-slate-300 leading-relaxed">
                                Unscheduled stop or unusual route departure detected. Are you safe?
                              </p>
                              <div className="text-center py-2">
                                <div className="text-2xl font-black text-amber-400 font-mono">
                                  {safetyCountdown}s
                                </div>
                                <div className="text-[10px] text-slate-400">Auto-SOS will trigger if not confirmed</div>
                              </div>
                              <div className="grid grid-cols-2 gap-2">
                                <button
                                  id="im-safe-btn"
                                  onClick={handleResolveAnomaly}
                                  className="py-2 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-[11px] cursor-pointer"
                                >
                                  I am Safe
                                </button>
                                <button
                                  id="escalate-sos-btn"
                                  onClick={handleTriggerSOS}
                                  className="py-2 rounded-xl bg-rose-600 hover:bg-rose-500 text-white font-bold text-[11px] cursor-pointer"
                                >
                                  Trigger SOS
                                </button>
                              </div>
                            </div>
                          )}

                          <div className="pt-2 space-y-2">
                            <button
                              id="mobile-stop-journey"
                              onClick={handleStopJourney}
                              className="w-full py-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 font-semibold text-xs border border-slate-700 cursor-pointer"
                            >
                              End Journey Safely
                            </button>
                            <button
                              id="mobile-quick-sos"
                              onClick={handleTriggerSOS}
                              className="w-full py-2.5 rounded-xl bg-rose-600 hover:bg-rose-500 text-white font-bold text-xs cursor-pointer"
                            >
                              Emergency SOS
                            </button>
                          </div>
                        </>
                      ) : (
                        <div className="text-center py-10 space-y-3">
                          <div className="w-12 h-12 rounded-full bg-slate-900 border border-slate-800 flex items-center justify-center mx-auto text-slate-500">
                            <Navigation className="w-6 h-6" />
                          </div>
                          <div className="text-xs font-semibold text-slate-300">No Active Journey</div>
                          <p className="text-[11px] text-slate-500 max-w-xs mx-auto">
                            Go to the Journey tab to set up a destination and enable real-time anomaly tracking.
                          </p>
                          <button
                            onClick={() => setActiveTab('journey')}
                            className="px-4 py-2 rounded-xl bg-rose-600 text-white text-xs font-bold cursor-pointer"
                          >
                            Set Destination
                          </button>
                        </div>
                      )}
                    </div>
                  )}

                  {/* TAB 3: GUARDIANS */}
                  {activeTab === 'guardians' && (
                    <div className="space-y-4">
                      <div className="text-xs font-bold text-slate-300">Trusted Contacts ({contacts.length})</div>
                      <div className="space-y-2 max-h-48 overflow-y-auto pr-1">
                        {contacts.map((c) => (
                          <div
                            key={c.id}
                            className="p-2.5 rounded-xl bg-slate-900 border border-slate-800 flex items-center justify-between text-xs"
                          >
                            <div>
                              <div className="font-bold text-slate-200">{c.name}</div>
                              <div className="text-[10px] text-slate-400">{c.phone}</div>
                            </div>
                            <button
                              onClick={() => handleDeleteContact(c.id)}
                              className="text-slate-500 hover:text-rose-400 text-[10px] cursor-pointer"
                            >
                              Remove
                            </button>
                          </div>
                        ))}
                      </div>

                      <form onSubmit={handleAddContact} className="bg-slate-900/80 p-3 rounded-xl border border-slate-800 space-y-2 text-xs">
                        <div className="font-semibold text-slate-300 text-[11px]">Add New Guardian</div>
                        <input
                          id={nameInputId}
                          type="text"
                          placeholder="Contact Name"
                          value={newContactName}
                          onChange={(e) => setNewContactName(e.target.value)}
                          className="w-full bg-slate-950 border border-slate-800 rounded-lg px-2.5 py-1.5 text-white text-xs"
                        />
                        <input
                          id={relInputId}
                          type="text"
                          placeholder="Relationship (e.g. Sister, Friend)"
                          value={newContactRel}
                          onChange={(e) => setNewContactRel(e.target.value)}
                          className="w-full bg-slate-950 border border-slate-800 rounded-lg px-2.5 py-1.5 text-white text-xs"
                        />
                        <input
                          id={phoneInputId}
                          type="tel"
                          placeholder="Phone Number (+91...)"
                          value={newContactPhone}
                          onChange={(e) => setNewContactPhone(e.target.value)}
                          className="w-full bg-slate-950 border border-slate-800 rounded-lg px-2.5 py-1.5 text-white text-xs"
                        />
                        <button
                          type="submit"
                          className="w-full py-1.5 rounded-lg bg-rose-600 hover:bg-rose-500 text-white font-bold text-xs cursor-pointer"
                        >
                          Save Contact
                        </button>
                      </form>
                    </div>
                  )}

                  {/* TAB 4: SOS TRIGGER */}
                  {activeTab === 'sos' && (
                    <div className="space-y-4">
                      {isSosTriggered ? (
                        <div className="p-4 rounded-2xl bg-rose-950/80 border-2 border-rose-500 text-center space-y-3 animate-pulse">
                          <BellRing className="w-10 h-10 text-rose-400 mx-auto" />
                          <div className="text-sm font-black text-rose-300">EMERGENCY SOS ACTIVE</div>
                          <p className="text-[11px] text-slate-300 leading-relaxed">
                            Live location + distress SMS broadcasted to all {contacts.length} guardians. Police (112) alerted.
                          </p>
                          <button
                            id="mobile-cancel-sos"
                            onClick={handleCancelSOS}
                            className="w-full py-2.5 rounded-xl bg-slate-900 border border-slate-700 text-slate-200 text-xs font-bold cursor-pointer"
                          >
                            Cancel Alarm (PIN Required)
                          </button>
                        </div>
                      ) : (
                        <div className="text-center py-6 space-y-4">
                          <button
                            id="mobile-sos-big-btn"
                            onClick={handleTriggerSOS}
                            className="w-32 h-32 rounded-full bg-gradient-to-tr from-rose-600 to-red-500 hover:from-rose-500 hover:to-red-400 text-white font-black text-lg shadow-2xl shadow-rose-600/50 flex flex-col items-center justify-center mx-auto transition-transform active:scale-90 border-4 border-rose-400/40 cursor-pointer"
                          >
                            <Shield className="w-8 h-8 mb-1" />
                            <span>SOS</span>
                          </button>
                          <div className="text-xs text-slate-400 max-w-xs mx-auto">
                            Pressing the SOS button sends immediate GPS coordinates to your guardians and begins emergency audio recording.
                          </div>
                        </div>
                      )}
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>

      {/* Footer */}
      <footer className="border-t border-slate-900 bg-slate-950 py-6 text-center text-xs text-slate-500">
        <div className="max-w-7xl mx-auto px-4 flex flex-col sm:flex-row items-center justify-between gap-3">
          <div className="flex items-center space-x-2">
            <Shield className="w-4 h-4 text-rose-500" />
            <span className="font-semibold text-slate-400">Abhaya Kavach • Proactive Safety App</span>
          </div>
          <div className="flex items-center space-x-4">
            <a
              id="footer-download-apk"
              href="/abhaya-kavach.apk"
              download="abhaya-kavach.apk"
              className="text-rose-400 hover:text-rose-300 font-semibold"
            >
              Download APK
            </a>
            <span>•</span>
            <span className="text-slate-500">Android 7.0+ (API 24-35)</span>
          </div>
        </div>
      </footer>
    </div>
  );
}
